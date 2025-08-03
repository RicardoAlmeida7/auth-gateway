package com.zerotrust.auth_gateway.infrastructure.web.controller;

import com.zerotrust.auth_gateway.application.dto.request.password.PasswordResetEmailRequest;
import com.zerotrust.auth_gateway.application.dto.request.password.PasswordResetRequest;
import com.zerotrust.auth_gateway.application.usecase.interfaces.auth.PasswordResetUseCase;
import com.zerotrust.auth_gateway.infrastructure.config.security.Ratelimited;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/password-reset")
public class PasswordResetController {

    private final PasswordResetUseCase passwordResetUseCase;

    public PasswordResetController(PasswordResetUseCase passwordResetUseCase) {
        this.passwordResetUseCase = passwordResetUseCase;
    }

    @Ratelimited
    @PostMapping("/request")
    public ResponseEntity<Void> requestReset(@RequestBody(required = false) PasswordResetEmailRequest request) {
        passwordResetUseCase.sendResetLink(request);
        return ResponseEntity.noContent().build();
    }

    @Ratelimited
    @PostMapping
    public ResponseEntity<Void> resetPassword(@RequestParam String token, @RequestBody(required = false) PasswordResetRequest request) {
        passwordResetUseCase.resetPassword(token, request);
        return ResponseEntity.noContent().build();
    }
}
