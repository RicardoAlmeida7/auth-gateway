package com.zerotrust.auth_gateway.infrastructure.web.controller;

import com.zerotrust.auth_gateway.application.dto.request.RegisterRequest;
import com.zerotrust.auth_gateway.application.dto.response.ManagedUserResponse;
import com.zerotrust.auth_gateway.application.usecase.interfaces.UserManagementUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final UserManagementUseCase userManagementUseCase;

    public AdminController(
            UserManagementUseCase userManagementUseCase
    ) {
        this.userManagementUseCase = userManagementUseCase;
    }

    @PostMapping("/create-user")
    public ResponseEntity<ManagedUserResponse> createUser(@RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userManagementUseCase.createUser(request));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@RequestParam String id) {
        userManagementUseCase.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ManagedUserResponse>> getUsers() {
        return ResponseEntity.ok(userManagementUseCase.getUsers());
    }
}
