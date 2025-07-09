package com.zerotrust.auth_gateway.application.usecase.implementations;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.zerotrust.auth_gateway.application.usecase.interfaces.ActivateAccountUseCase;
import com.zerotrust.auth_gateway.domain.exception.FirstAccessPasswordRequiredException;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.domain.validation.PasswordValidator;
import com.zerotrust.auth_gateway.infrastructure.security.jwt.JwtTokenGenerator;
import com.zerotrust.auth_gateway.infrastructure.web.dto.PasswordResetRequest;

public class ActivateAccountUseCaseImpl implements ActivateAccountUseCase {

    private final JwtTokenGenerator jwtTokenGenerator;
    private final UserRepository userRepository;

    public ActivateAccountUseCaseImpl(JwtTokenGenerator jwtTokenGenerator, UserRepository userRepository) {
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.userRepository = userRepository;
    }

    @Override
    public void activate(String token, PasswordResetRequest request) {
        DecodedJWT decodedJWT = jwtTokenGenerator.verifyToken(token);
        String userName = decodedJWT.getSubject();

        User user = userRepository.findByUsername(userName).orElseThrow(() -> new IllegalArgumentException("User not found."));

        if (user.isFirstAccessRequired()) {
            if (request == null) {
                throw new FirstAccessPasswordRequiredException("Password reset is required on first access. Please provide a new password and confirmation.");
            }
            PasswordValidator.validate(request.getNewPassword(), request.getConfirmPassword());
            user.setFirstAccessRequired(false);
        }

        user.setEnabled(true);
        userRepository.save(user);
    }
}
