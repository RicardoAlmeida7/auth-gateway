package com.zerotrust.auth_gateway.domain.service.implementation;

import com.zerotrust.auth_gateway.domain.exception.UserProfileUpdateException;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.domain.service.interfaces.UserUpdateValidator;
import com.zerotrust.auth_gateway.domain.validation.PasswordValidator;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserUpdateValidatorImpl implements UserUpdateValidator {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserUpdateValidatorImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void updateUsernameIfNeeded(User user, String newUsername) {
        if (newUsername != null && !newUsername.isBlank() && !newUsername.equals(user.getUsername())) {
            userRepository.findByUsername(newUsername).ifPresent(existing -> {
                throw new UserProfileUpdateException("Username already taken.");
            });
            user.setUsername(newUsername);
        }
    }

    @Override
    public void updateEmailIfNeeded(User user, String newEmail) {
        if (newEmail != null && !newEmail.isBlank() && !newEmail.equals(user.getEmail())) {
            userRepository.findByEmail(newEmail).ifPresent(existing -> {
                throw new UserProfileUpdateException("Email already in use.");
            });
            user.setEmail(newEmail);
        }
    }

    @Override
    public void updatePasswordIfNeeded(User user, String newPassword, String confirmPassword) {
        if (newPassword != null && !newPassword.isBlank()) {
            PasswordValidator.validate(newPassword, confirmPassword);
            user.setPasswordHash(passwordEncoder.encode(newPassword));
        }
    }
}
