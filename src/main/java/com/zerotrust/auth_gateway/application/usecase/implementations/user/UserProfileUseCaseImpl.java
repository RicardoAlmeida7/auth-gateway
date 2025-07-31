package com.zerotrust.auth_gateway.application.usecase.implementations.user;

import com.zerotrust.auth_gateway.application.dto.request.user.UpdateProfileRequest;
import com.zerotrust.auth_gateway.application.usecase.interfaces.user.UserProfileUseCase;
import com.zerotrust.auth_gateway.domain.exception.UserNotFoundException;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.domain.service.interfaces.UserUpdateValidator;

public class UserProfileUseCaseImpl implements UserProfileUseCase {

    private final UserRepository userRepository;
    private final UserUpdateValidator userUpdateValidator;

    public UserProfileUseCaseImpl(UserRepository userRepository, UserUpdateValidator userUpdateValidator) {
        this.userRepository = userRepository;
        this.userUpdateValidator = userUpdateValidator;
    }

    @Override
    public void updateProfile(String userId, UpdateProfileRequest request) {
        User user = userRepository.findByUsername(userId)
                .or(() -> userRepository.findByEmail(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        userUpdateValidator.updateUsernameIfNeeded(user, request.username());
        userUpdateValidator.updateEmailIfNeeded(user, request.email());
        userUpdateValidator.updatePasswordIfNeeded(user, request.newPassword(), request.confirmPassword());

        userRepository.save(user);
    }
}
