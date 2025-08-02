package com.zerotrust.auth_gateway.application.usecase.implementations.auth;

import com.zerotrust.auth_gateway.application.dto.request.password.PasswordResetEmailRequest;
import com.zerotrust.auth_gateway.application.dto.request.password.PasswordResetRequest;
import com.zerotrust.auth_gateway.application.service.interfaces.JwtTokenService;
import com.zerotrust.auth_gateway.application.usecase.interfaces.auth.PasswordResetUseCase;
import com.zerotrust.auth_gateway.domain.exception.PasswordResetException;
import com.zerotrust.auth_gateway.domain.exception.UserNotFoundException;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.domain.service.interfaces.EmailService;
import com.zerotrust.auth_gateway.domain.validation.PasswordValidator;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordResetUseCaseImpl implements PasswordResetUseCase {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public PasswordResetUseCaseImpl(
            UserRepository userRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder, JwtTokenService jwtTokenService) {

        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public void sendResetLink(PasswordResetEmailRequest request) {
        if (request == null) {
            throw new PasswordResetException("Email is required for reset.");
        }
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException("User not found."));
        user.setEnabled(false);
        userRepository.save(user);

        String token = jwtTokenService.generateResetPasswordToken(user);
        String link = "http://localhost:8080/api/v1/users/password-reset?token=" + token;
        emailService.sendResetPasswordEmail(request.email(), link);
    }

    @Override
    public void resetPassword(String token, PasswordResetRequest request) {
        String username = jwtTokenService.validateResetPasswordToken(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new PasswordResetException("User not found for password reset."));

        if (request == null) {
            throw new PasswordResetException("Password and confirmation are required for reset.");
        }
        PasswordValidator.validate(request.getNewPassword(), request.getConfirmPassword());

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setEnabled(true);
        userRepository.save(user);
        jwtTokenService.blacklistResetPasswordToken(token);
    }
}
