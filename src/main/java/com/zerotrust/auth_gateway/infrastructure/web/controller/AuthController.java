package com.zerotrust.auth_gateway.infrastructure.web.controller;

import com.zerotrust.auth_gateway.application.dto.request.AuthenticationRequest;
import com.zerotrust.auth_gateway.application.dto.request.RefreshTokenRequest;
import com.zerotrust.auth_gateway.application.dto.response.JwtResponse;
import com.zerotrust.auth_gateway.application.dto.response.UserLoginInfoResponse;
import com.zerotrust.auth_gateway.application.usecase.interfaces.UserLoginUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserLoginUseCase userLoginUseCase;

    public AuthController(UserLoginUseCase userLoginUseCase) {
        this.userLoginUseCase = userLoginUseCase;
    }

    @GetMapping("/login")
    public ResponseEntity<UserLoginInfoResponse> getUserLoginInfo(@RequestParam String userId) {
        return ResponseEntity.ok(userLoginUseCase.getUserStatusInfo(userId));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(userLoginUseCase.login(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<JwtResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(userLoginUseCase.refreshToken(request));
    }
}
