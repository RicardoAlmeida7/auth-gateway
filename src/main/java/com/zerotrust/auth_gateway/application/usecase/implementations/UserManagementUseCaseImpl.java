package com.zerotrust.auth_gateway.application.usecase.implementations;

import com.zerotrust.auth_gateway.application.dto.request.DeleteUserRequest;
import com.zerotrust.auth_gateway.application.usecase.interfaces.UserManagementUseCase;
import com.zerotrust.auth_gateway.domain.exception.UserNotFoundException;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;

public class UserManagementUseCaseImpl implements UserManagementUseCase {

    private final UserRepository userRepository;

    public UserManagementUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void deleteUser(DeleteUserRequest request) {
        User user = userRepository.findByUsername(request.userId()).or(() ->
                userRepository.findByEmail(request.userId()))
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        userRepository.delete(user);
    }
}
