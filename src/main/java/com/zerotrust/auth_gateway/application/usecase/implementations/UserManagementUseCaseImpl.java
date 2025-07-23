package com.zerotrust.auth_gateway.application.usecase.implementations;

import com.zerotrust.auth_gateway.application.usecase.interfaces.UserManagementUseCase;
import com.zerotrust.auth_gateway.domain.exception.UserNotFoundException;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;

import java.util.UUID;

public class UserManagementUseCaseImpl implements UserManagementUseCase {

    private final UserRepository userRepository;

    public UserManagementUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void deleteUser(String id) {
        try {
            UUID userId = UUID.fromString(id);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found."));

            userRepository.delete(user);
        } catch (IllegalArgumentException e) {
            throw new UserNotFoundException("Invalid user id.");
        }
    }
}
