package com.zerotrust.auth_gateway.infrastructure.web.controller;

import com.zerotrust.auth_gateway.application.dto.request.registration.RegisterRequest;
import com.zerotrust.auth_gateway.application.dto.request.user.AdminUpdateUserRequest;
import com.zerotrust.auth_gateway.application.dto.response.user.ManagedUserResponse;
import com.zerotrust.auth_gateway.application.usecase.interfaces.admin.AdminUserManagementUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/admin/users")
public class AdminController {

    private final AdminUserManagementUseCase adminUserManagementUseCase;

    public AdminController(
            AdminUserManagementUseCase adminUserManagementUseCase
    ) {
        this.adminUserManagementUseCase = adminUserManagementUseCase;
    }

    @PostMapping
    public ResponseEntity<ManagedUserResponse> createUser(@RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminUserManagementUseCase.createUser(request));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        adminUserManagementUseCase.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ManagedUserResponse>> getUsers() {
        return ResponseEntity.ok(adminUserManagementUseCase.getUsers());
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ManagedUserResponse> updateUser(@PathVariable String userId, @RequestBody AdminUpdateUserRequest request) {
        return ResponseEntity.ok(adminUserManagementUseCase.updateUser(userId, request));
    }
}
