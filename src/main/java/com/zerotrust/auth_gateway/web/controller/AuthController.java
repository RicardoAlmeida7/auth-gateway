package com.zerotrust.auth_gateway.web.controller;

import com.zerotrust.auth_gateway.application.usecase.RegisterUserUseCase;
import com.zerotrust.auth_gateway.application.service.AuthService;
import com.zerotrust.auth_gateway.web.dto.JwtResponse;
import com.zerotrust.auth_gateway.web.dto.LoginRequest;
import com.zerotrust.auth_gateway.web.dto.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final RegisterUserUseCase registerUserUseCase;
    private final AuthService authService;

    public AuthController(RegisterUserUseCase registerUserUseCase, AuthService authService) {
        this.registerUserUseCase = registerUserUseCase;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
        registerUserUseCase.register(request);
        // TODO: Add input validation and return appropriate status codes in future iterations
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {
        String token = authService.login(request.getUsername(), request.getPassword());
        // TODO: Add error handling for authentication failures
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
