package com.zerotrust.auth_gateway.web.controller;

import com.zerotrust.auth_gateway.application.usecase.interfaces.AuthServiceUseCase;
import com.zerotrust.auth_gateway.application.usecase.interfaces.RegisterUserUseCase;
import com.zerotrust.auth_gateway.application.usecase.implementations.AuthServiceUseCaseImpl;
import com.zerotrust.auth_gateway.web.dto.JwtResponse;
import com.zerotrust.auth_gateway.web.dto.LoginRequest;
import com.zerotrust.auth_gateway.web.dto.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final RegisterUserUseCase registerUserUseCase;
    private final AuthServiceUseCase authServiceUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase, AuthServiceUseCase authServiceUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.authServiceUseCase = authServiceUseCase;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
        registerUserUseCase.register(request);
        // TODO: Add input validation and return appropriate status codes in future iterations
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {
        String token = authServiceUseCase.login(request.getUsername(), request.getPassword());
        // TODO: Add error handling for authentication failures
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
