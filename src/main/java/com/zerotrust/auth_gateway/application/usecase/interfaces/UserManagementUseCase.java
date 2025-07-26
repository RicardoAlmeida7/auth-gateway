package com.zerotrust.auth_gateway.application.usecase.interfaces;

import com.zerotrust.auth_gateway.application.dto.request.RegisterRequest;
import com.zerotrust.auth_gateway.application.dto.response.ManagedUserResponse;

import java.util.List;

public interface UserManagementUseCase {
    void deleteUser(String id);
    List<ManagedUserResponse> getUsers();
    ManagedUserResponse createUser(RegisterRequest request);
}
