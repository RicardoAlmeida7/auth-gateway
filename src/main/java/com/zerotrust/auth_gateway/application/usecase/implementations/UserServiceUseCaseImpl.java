package com.zerotrust.auth_gateway.application.usecase.implementations;

import com.zerotrust.auth_gateway.application.dto.request.RegisterRequest;
import com.zerotrust.auth_gateway.application.dto.request.ResendActivationRequest;
import com.zerotrust.auth_gateway.application.usecase.interfaces.UserServiceUseCase;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.domain.service.EmailService;
import com.zerotrust.auth_gateway.domain.service.TOTPService;
import com.zerotrust.auth_gateway.domain.validation.EmailValidator;
import com.zerotrust.auth_gateway.domain.validation.PasswordValidator;
import com.zerotrust.auth_gateway.domain.validation.RoleValidator;
import com.zerotrust.auth_gateway.domain.validation.UsernameValidator;
import com.zerotrust.auth_gateway.infrastructure.security.jwt.JwtTokenGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;
// TODO: Create use case to handle MFA actions (enable, disable, reset)
public class UserServiceUseCaseImpl implements UserServiceUseCase {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TOTPService totpService;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final EmailService emailService;

    public UserServiceUseCaseImpl(PasswordEncoder passwordEncoder, UserRepository userRepository, TOTPService totpService, JwtTokenGenerator jwtTokenGenerator, EmailService emailService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.totpService = totpService;
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
                request.isFirstAccessRequired()
        );

        String qrCodeUrl = prepareMfaIfEnabled(user);
        userRepository.save(user);
        sendActivationEmail(user, qrCodeUrl);
    }

    @Override
    public void resendActivationEmail(ResendActivationRequest request) {
        // TODO: Validate old activation token to prevent reusing expired or previously used tokens for activation
        // TODO: Create a specific UserNotFoundException
        if (request == null) throw new IllegalArgumentException("No user found with provided email or username.");
        User user = findUserByEmailOrUsername(request);
        String qrCodeUrl = prepareMfaIfEnabled(user);
        user.setEnabled(false);
        userRepository.save(user);
        sendActivationEmail(user, qrCodeUrl);
    }

    private void validateRegisterRequest(RegisterRequest request) {
        if (request == null) throw new IllegalArgumentException("No user found with provided email or username.");
        UsernameValidator.validate(request.getUsername());
        PasswordValidator.validate(request.getPassword(), request.getConfirmPassword());
        EmailValidator.validate(request.getEmail());
        RoleValidator.validate(request.getRoles());
    }

    private String prepareMfaIfEnabled(User user) {
        if (!user.isMfaEnabled()) return null;

        String secret = totpService.generateSecret();
        String qrCodeUrl = totpService.generateQrCodeUrl(user.getUsername(), secret);
        user.setMfaSecret(secret);
        return qrCodeUrl;
    }

    private User findUserByEmailOrUsername(ResendActivationRequest request) {
        return userRepository.findByEmail(request.email())
                .or(() -> userRepository.findByUsername(request.username()))
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    private void sendActivationEmail(User user, String qrCodeUrl) {
        String activationToken = jwtTokenGenerator.generateActivationToken(user.getUsername(), user.getEmail());
        // TODO: Move base URL to an environment variable or configuration file
        String activationLink = "http://localhost:8080/api/v1/user/activate?token=" + activationToken;
        emailService.sendActivationEmail(user.getEmail(), activationToken, activationLink, qrCodeUrl);
    }
}
