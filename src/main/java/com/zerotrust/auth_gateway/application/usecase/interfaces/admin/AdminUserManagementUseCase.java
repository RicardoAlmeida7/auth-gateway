package com.zerotrust.auth_gateway.application.usecase.interfaces.admin;

import com.zerotrust.auth_gateway.application.dto.request.registration.RegisterRequest;
import com.zerotrust.auth_gateway.application.dto.request.user.AdminUpdateUserRequest;
import com.zerotrust.auth_gateway.application.dto.response.user.ListManagedUserResponse;
import com.zerotrust.auth_gateway.application.dto.response.user.ManagedUserResponse;

import java.util.List;

public interface AdminUserManagementUseCase {
    void deleteUser(String id);
    void blockUser(String id);
    void unblockUser(String id);
    List<ListManagedUserResponse> getUsers();
    ManagedUserResponse createUser(RegisterRequest request);
    ManagedUserResponse updateUser(String userId, AdminUpdateUserRequest request);
    ManagedUserResponse getUser(String userId);
}
