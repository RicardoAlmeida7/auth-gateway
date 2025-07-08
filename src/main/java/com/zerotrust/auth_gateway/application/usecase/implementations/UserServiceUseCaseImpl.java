package com.zerotrust.auth_gateway.application.usecase.implementations;

import com.zerotrust.auth_gateway.application.usecase.interfaces.UserServiceUseCase;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.domain.service.EmailService;
import com.zerotrust.auth_gateway.domain.service.TOTPService;
import com.zerotrust.auth_gateway.domain.validation.EmailValidator;
import com.zerotrust.auth_gateway.domain.validation.PasswordValidator;
import com.zerotrust.auth_gateway.domain.validation.RoleValidator;
import com.zerotrust.auth_gateway.domain.validation.UsernameValidator;
import com.zerotrust.auth_gateway.infrastructure.security.jwt.JwtTokenGenerator;
import com.zerotrust.auth_gateway.web.dto.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

public class UserServiceUseCaseImpl implements UserServiceUseCase {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository useRepositoryPort;
    private final TOTPService totpService;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final EmailService emailService;

    public UserServiceUseCaseImpl(PasswordEncoder passwordEncoder, UserRepository useRepositoryPort, TOTPService totpService, JwtTokenGenerator jwtTokenGenerator, EmailService emailService) {
        this.passwordEncoder = passwordEncoder;
        this.useRepositoryPort = useRepositoryPort;
        this.totpService = totpService;
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.emailService = emailService;
    }

    @Override
    public void register(RegisterRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("RegisterUserCommand cannot be null.");
        }

        UsernameValidator.validate(request.getUsername());
        PasswordValidator.validate(request.getPassword(), request.getConfirmPassword());
        EmailValidator.validate(request.getEmail());
        RoleValidator.validate(request.getRoles());

        String secret = "";
        String qrCodeUrl = null;
        if (request.isMfaEnabled()) {
            secret = totpService.generateSecret();
            qrCodeUrl = totpService.generateQrCodeUrl(request.getUsername(), secret);
        }

        String passwordHashed = passwordEncoder.encode(request.getPassword());
        User user = new User(
                UUID.randomUUID(),
                request.getUsername(),
                passwordHashed,
                request.getEmail(),
                request.isMfaEnabled(),
                secret,
                false,
                request.getRoles(),
                request.isFirstAccessRequired()
        );

        useRepositoryPort.save(user);

        String activationToken = jwtTokenGenerator.generateActivationToken(user.getUsername(), user.getEmail());
        String activationLink = "http://localhost:8080/api/v1/user/activate?token=" + activationToken;
        emailService.sendActivationEmail(user.getEmail(), activationToken, activationLink, qrCodeUrl);
    }
}
