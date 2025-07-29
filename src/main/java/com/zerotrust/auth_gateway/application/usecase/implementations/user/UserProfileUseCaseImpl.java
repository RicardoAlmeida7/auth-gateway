package com.zerotrust.auth_gateway.application.usecase.implementations.user;

import com.zerotrust.auth_gateway.application.dto.request.user.UpdateProfileRequest;
import com.zerotrust.auth_gateway.application.usecase.interfaces.user.UserProfileUseCase;
import com.zerotrust.auth_gateway.domain.exception.UserNotFoundException;
import com.zerotrust.auth_gateway.domain.exception.UserProfileUpdateException;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.domain.validation.PasswordValidator;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserProfileUseCaseImpl implements UserProfileUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserProfileUseCaseImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void updateProfile(String userId, UpdateProfileRequest request) {
        User user = userRepository.findByUsername(userId)
                .or(() -> userRepository.findByEmail(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        updateUsernameIfNeeded(user, request.username());
        updateEmailIfNeeded(user, request.email());
        updatePasswordIfNeeded(user, request);

        userRepository.save(user);
    }

    private void updateUsernameIfNeeded(User user, String newUsername) {
        if (newUsername != null && !newUsername.isBlank() && !newUsername.equals(user.getUsername())) {
            userRepository.findByUsername(newUsername).ifPresent(existing -> {
                throw new UserProfileUpdateException("Username already taken.");
            });
            user.setUsername(newUsername);
        }
    }

    private void updateEmailIfNeeded(User user, String newEmail) {
        if (newEmail != null && !newEmail.isBlank() && !newEmail.equals(user.getEmail())) {
            userRepository.findByEmail(newEmail).ifPresent(existing -> {
                throw new UserProfileUpdateException("Email already in use.");
            });
            user.setEmail(newEmail);
        }
    }

    private void updatePasswordIfNeeded(User user, UpdateProfileRequest request) {
        String newPassword = request.newPassword();
        if (newPassword != null && !newPassword.isBlank()) {
            PasswordValidator.validate(request.newPassword(), request.confirmPassword());
            user.setPasswordHash(passwordEncoder.encode(newPassword));
        }
    }
}
