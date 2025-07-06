package com.zerotrust.auth_gateway.web.controller;

import com.zerotrust.auth_gateway.application.usecase.interfaces.AuthServiceUseCase;
import com.zerotrust.auth_gateway.web.dto.JwtResponse;
import com.zerotrust.auth_gateway.web.dto.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthServiceUseCase authServiceUseCase;

    public AuthController(AuthServiceUseCase authServiceUseCase) {
        this.authServiceUseCase = authServiceUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {
        String token = authServiceUseCase.login(request.getUsername(), request.getPassword(), request.getOtp());
        // TODO: Add error handling for authentication failures
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
