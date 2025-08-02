package com.zerotrust.auth_gateway.infrastructure.web.controller;

import com.zerotrust.auth_gateway.application.dto.request.auth.AuthenticationRequest;
import com.zerotrust.auth_gateway.application.dto.response.auth.JwtResponse;
import com.zerotrust.auth_gateway.application.dto.response.auth.UserLoginInfoResponse;
import com.zerotrust.auth_gateway.application.usecase.interfaces.auth.AuthenticationUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationUseCase authenticationUseCase;

    public AuthController(AuthenticationUseCase authenticationUseCase) {
        this.authenticationUseCase = authenticationUseCase;
    }

    @GetMapping("/login")
    public ResponseEntity<UserLoginInfoResponse> getUserLoginInfo(@RequestParam String userId) {
        return ResponseEntity.ok(authenticationUseCase.getUserStatusInfo(userId));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationUseCase.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String accessTokenHeader,
            @RequestHeader("Refresh-Token") String refreshToken) {

        authenticationUseCase.logout(accessTokenHeader, refreshToken);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<JwtResponse> refreshToken(@RequestHeader("Refresh-Token") String refreshToken) {
        return ResponseEntity.ok(authenticationUseCase.refreshToken(refreshToken));
    }
}
