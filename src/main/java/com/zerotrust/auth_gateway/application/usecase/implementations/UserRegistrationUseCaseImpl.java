package com.zerotrust.auth_gateway.application.usecase.implementations;

import com.zerotrust.auth_gateway.application.dto.request.RegisterRequest;
import com.zerotrust.auth_gateway.application.dto.request.ResendActivationRequest;
import com.zerotrust.auth_gateway.application.usecase.interfaces.MfaManagementUseCase;
import com.zerotrust.auth_gateway.application.usecase.interfaces.UserRegistrationUseCase;
import com.zerotrust.auth_gateway.domain.exception.UserNotFoundException;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.domain.service.EmailService;
import com.zerotrust.auth_gateway.domain.validation.EmailValidator;
import com.zerotrust.auth_gateway.domain.validation.PasswordValidator;
import com.zerotrust.auth_gateway.domain.validation.RoleValidator;
import com.zerotrust.auth_gateway.domain.validation.UsernameValidator;
import com.zerotrust.auth_gateway.infrastructure.security.jwt.JwtTokenGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

public class UserRegistrationUseCaseImpl implements UserRegistrationUseCase {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final MfaManagementUseCase mfaManagementUseCase;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final EmailService emailService;

    public UserRegistrationUseCaseImpl(
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            MfaManagementUseCase mfaManagementUseCase,
            JwtTokenGenerator jwtTokenGenerator,
            EmailService emailService
    ) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.mfaManagementUseCase = mfaManagementUseCase;
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.emailService = emailService;
    }

    @Override
    public void register(RegisterRequest request) {
        validateRegisterRequest(request);

        String passwordHashed = passwordEncoder.encode(request.getPassword());
        User user = new User(
                UUID.randomUUID(),
                request.getUsername(),
                passwordHashed,
                request.getEmail(),
                request.isMfaEnabled(),
                null,
                false,
                request.getRoles(),
                false
        );

        String qrCodeUrl = mfaManagementUseCase.prepareMfaIfEnabled(user);
        userRepository.save(user);
        sendActivationEmail(user, qrCodeUrl);
    }

    @Override
    public void resendActivationEmail(ResendActivationRequest request) {
        // TODO: Validate old activation token to prevent reusing expired or previously used tokens for activation
        if (request == null) throw new UserNotFoundException("No user found with provided email or username.");
        User user = findUserByEmailOrUsername(request);
        String qrCodeUrl = mfaManagementUseCase.prepareMfaIfEnabled(user);
        user.setEnabled(false);
        userRepository.save(user);
        sendActivationEmail(user, qrCodeUrl);
    }

    private void validateRegisterRequest(RegisterRequest request) {
        if (request == null) throw new UserNotFoundException("No user found with provided email or username.");
        UsernameValidator.validate(request.getUsername());
        PasswordValidator.validate(request.getPassword(), request.getConfirmPassword());
        EmailValidator.validate(request.getEmail());
        RoleValidator.validate(request.getRoles());
    }

    private User findUserByEmailOrUsername(ResendActivationRequest request) {
        return userRepository.findByEmail(request.email())
                .or(() -> userRepository.findByUsername(request.username()))
                .orElseThrow(() -> new UserNotFoundException("User not found."));
    }

    private void sendActivationEmail(User user, String qrCodeUrl) {
        String activationToken = jwtTokenGenerator.generateActivationToken(user.getUsername(), user.getEmail());
        // TODO: Move base URL to an environment variable or configuration file
        String activationLink = "http://localhost:8080/api/v1/user/activate?token=" + activationToken;
        emailService.sendActivationEmail(user.getEmail(), activationToken, activationLink, qrCodeUrl);
    }
}
