package com.zerotrust.auth_gateway.infrastructure.web.controller;

import com.zerotrust.auth_gateway.application.usecase.interfaces.ActivateAccountUseCase;
import com.zerotrust.auth_gateway.application.usecase.interfaces.UserServiceUseCase;
import com.zerotrust.auth_gateway.infrastructure.web.dto.PasswordResetRequest;
import com.zerotrust.auth_gateway.infrastructure.web.dto.RegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserServiceUseCase useServiceUseCase;
    private final ActivateAccountUseCase activateAccountUseCase;

    public UserController(UserServiceUseCase useServiceUseCase, ActivateAccountUseCase activateAccountUseCase) {
        this.useServiceUseCase = useServiceUseCase;
        this.activateAccountUseCase = activateAccountUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
        RegisterRequest safeRequest = new RegisterRequest(
                request.getUsername(),
                request.getPassword(),
                request.getEmail(),
                request.isMfaEnabled(),
                request.getConfirmPassword(),
                false
        );
        useServiceUseCase.register(safeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-user")
    public ResponseEntity<Void> createUser(@RequestBody RegisterRequest request) {
        request.setFirstAccessRequired(true);
        useServiceUseCase.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/activate")
    public ResponseEntity<Void> activateAccount(@RequestParam String token, @RequestBody(required = false) PasswordResetRequest request) {
        activateAccountUseCase.activate(token, request);
        return ResponseEntity.ok().build();
    }
}
