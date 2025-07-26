package com.zerotrust.auth_gateway.application.usecase.interfaces;

import com.zerotrust.auth_gateway.application.dto.response.AdminUserListResponse;

import java.util.List;

public interface UserManagementUseCase {
    void deleteUser(String id);
    List<AdminUserListResponse> getUsers();
}
