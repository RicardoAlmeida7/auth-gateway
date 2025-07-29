package com.zerotrust.auth_gateway.application.usecase.implementations.activation;

import com.zerotrust.auth_gateway.application.service.interfaces.JwtTokenService;
import com.zerotrust.auth_gateway.application.usecase.interfaces.activation.AccountActivationUseCase;
import com.zerotrust.auth_gateway.domain.exception.FirstAccessPasswordRequiredException;
import com.zerotrust.auth_gateway.domain.exception.UserNotFoundException;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.domain.validation.PasswordValidator;
import com.zerotrust.auth_gateway.application.dto.request.password.PasswordResetRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AccountActivationUseCaseImpl implements AccountActivationUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AccountActivationUseCaseImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder, JwtTokenService jwtTokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public void activate(String token, PasswordResetRequest request) {
        String userName = jwtTokenService.validateActivationToken(token);

        User user = userRepository.findByUsername(userName).orElseThrow(() -> new UserNotFoundException("User not found."));

        if (user.isFirstAccessRequired()) {
            if (request == null) {
                throw new FirstAccessPasswordRequiredException("Password reset is required on first access. Please provide a new password and confirmation.");
            }
            PasswordValidator.validate(request.getNewPassword(), request.getConfirmPassword());
            String newPassword = passwordEncoder.encode(request.getNewPassword());
            user.setPasswordHash(newPassword);
            user.setFirstAccessRequired(false);
        }

        user.setEnabled(true);
        userRepository.save(user);
        jwtTokenService.blacklistActivationToken(token);
    }
}
