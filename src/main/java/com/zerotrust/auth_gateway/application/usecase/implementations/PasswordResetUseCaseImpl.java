package com.zerotrust.auth_gateway.application.usecase.implementations;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.zerotrust.auth_gateway.application.dto.request.PasswordResetEmailRequest;
import com.zerotrust.auth_gateway.application.dto.request.PasswordResetRequest;
import com.zerotrust.auth_gateway.application.usecase.interfaces.PasswordResetUseCase;
import com.zerotrust.auth_gateway.domain.exception.PasswordResetException;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.domain.service.EmailService;
import com.zerotrust.auth_gateway.domain.validation.PasswordValidator;
import com.zerotrust.auth_gateway.infrastructure.security.jwt.JwtTokenGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordResetUseCaseImpl implements PasswordResetUseCase {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetUseCaseImpl(
            UserRepository userRepository,
            EmailService emailService,
            JwtTokenGenerator jwtTokenGenerator,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.emailService = emailService;
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void sendResetLink(PasswordResetEmailRequest request) {
        if (request == null) {
            throw new PasswordResetException("Email is required for reset.");
        }
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        user.setEnabled(false);
        userRepository.save(user);

        String token = jwtTokenGenerator.generateToken(user.getUsername(), user.getRoles());
        String link = "http://localhost:8080/api/v1/user/reset-password?token=" + token;
        emailService.sendResetPasswordEmail(request.email(), link);
    }

    @Override
    public void resetPassword(String token, PasswordResetRequest request) {
        DecodedJWT decodedJWT = jwtTokenGenerator.verifyToken(token);
        String username = decodedJWT.getSubject();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new PasswordResetException("User not found for password reset."));

        if (request == null) {
            throw new PasswordResetException("Password and confirmation are required for reset.");
        }
        PasswordValidator.validate(request.getNewPassword(), request.getConfirmPassword());

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setEnabled(true);
        userRepository.save(user);
    }
}
