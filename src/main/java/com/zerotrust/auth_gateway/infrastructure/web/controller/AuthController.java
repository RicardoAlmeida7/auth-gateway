package com.zerotrust.auth_gateway.infrastructure.web.controller;

import com.zerotrust.auth_gateway.application.dto.request.AuthenticationRequest;
import com.zerotrust.auth_gateway.application.usecase.interfaces.AuthServiceUseCase;
import com.zerotrust.auth_gateway.application.dto.response.JwtResponse;
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
    public ResponseEntity<JwtResponse> login(@RequestBody AuthenticationRequest request) {
        String token = authServiceUseCase.login(request);
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
