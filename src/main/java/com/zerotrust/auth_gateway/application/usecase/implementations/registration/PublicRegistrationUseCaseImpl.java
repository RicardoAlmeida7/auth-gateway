package com.zerotrust.auth_gateway.application.usecase.implementations.registration;

import com.zerotrust.auth_gateway.application.dto.request.registration.RegisterRequest;
import com.zerotrust.auth_gateway.application.dto.request.registration.ResendActivationRequest;
import com.zerotrust.auth_gateway.application.service.interfaces.JwtTokenService;
import com.zerotrust.auth_gateway.application.usecase.interfaces.auth.MfaManagementUseCase;
import com.zerotrust.auth_gateway.application.usecase.interfaces.registration.PublicRegistrationUseCase;
import com.zerotrust.auth_gateway.domain.enums.Role;
import com.zerotrust.auth_gateway.domain.exception.UserNotFoundException;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.domain.service.interfaces.EmailService;
import com.zerotrust.auth_gateway.domain.validation.EmailValidator;
import com.zerotrust.auth_gateway.domain.validation.PasswordValidator;
import com.zerotrust.auth_gateway.domain.validation.UsernameValidator;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

public class PublicRegistrationUseCaseImpl implements PublicRegistrationUseCase {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final MfaManagementUseCase mfaManagementUseCase;
    private final EmailService emailService;
    private final JwtTokenService jwtTokenService;

    public PublicRegistrationUseCaseImpl(
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            MfaManagementUseCase mfaManagementUseCase,
            EmailService emailService, JwtTokenService jwtTokenService
    ) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.mfaManagementUseCase = mfaManagementUseCase;
        this.emailService = emailService;
        this.jwtTokenService = jwtTokenService;
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
                 List.of(Role.ROLE_USER.name()),
                false
        );

        String qrCodeUrl = mfaManagementUseCase.generateMfaSetupIfEnabled(user);
        userRepository.save(user);
        sendActivationEmail(user, qrCodeUrl);
    }

    @Override
    public void resendActivationEmail(ResendActivationRequest request) {
        if (request == null) throw new UserNotFoundException("No user found with provided email or username.");
        User user = findUserByEmailOrUsername(request);
        String qrCodeUrl = mfaManagementUseCase.generateMfaSetupIfEnabled(user);
        user.setEnabled(false);
        userRepository.save(user);
        sendActivationEmail(user, qrCodeUrl);
    }

    private void validateRegisterRequest(RegisterRequest request) {
        if (request == null) throw new UserNotFoundException("No user found with provided email or username.");
        UsernameValidator.validate(request.getUsername());
        PasswordValidator.validate(request.getPassword(), request.getConfirmPassword());
        EmailValidator.validate(request.getEmail());
    }

    private User findUserByEmailOrUsername(ResendActivationRequest request) {
        return userRepository.findByEmail(request.email())
                .or(() -> userRepository.findByUsername(request.username()))
                .orElseThrow(() -> new UserNotFoundException("User not found."));
    }

    private void sendActivationEmail(User user, String qrCodeUrl) {
        String activationToken = jwtTokenService.generateActivationToken(user);
        // TODO: Move base URL to an environment variable or configuration file
        String activationLink = "http://localhost:8080/api/v1/user/activate?token=" + activationToken;
        emailService.sendActivationEmail(user.getEmail(), activationToken, activationLink, qrCodeUrl);
    }
}
