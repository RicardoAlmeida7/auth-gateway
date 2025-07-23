package com.zerotrust.auth_gateway.infrastructure.web.controller;

import com.zerotrust.auth_gateway.application.dto.request.RegisterRequest;
import com.zerotrust.auth_gateway.application.usecase.interfaces.UserManagementUseCase;
import com.zerotrust.auth_gateway.application.usecase.interfaces.UserRegistrationUse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final UserRegistrationUse userRegistrationUse;
    private final UserManagementUseCase userManagementUseCase;

    public AdminController(
            UserRegistrationUse userRegistrationUse,
            UserManagementUseCase userManagementUseCase
    ) {
        this.userRegistrationUse = userRegistrationUse;
        this.userManagementUseCase = userManagementUseCase;
    }

    @PostMapping("/create-user")
    public ResponseEntity<Void> createUser(@RequestBody RegisterRequest request) {
        request.setFirstAccessRequired(true);
        userRegistrationUse.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@RequestParam String id) {
        userManagementUseCase.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
