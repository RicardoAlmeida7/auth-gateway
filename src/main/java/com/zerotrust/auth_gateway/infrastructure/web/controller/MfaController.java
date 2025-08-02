package com.zerotrust.auth_gateway.infrastructure.web.controller;

import com.zerotrust.auth_gateway.application.usecase.interfaces.auth.MfaManagementUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/me/mfa")
public class MfaController {

    private final MfaManagementUseCase mfaManagementUseCase;

    public MfaController(MfaManagementUseCase mfaManagementUseCase) {
        this.mfaManagementUseCase = mfaManagementUseCase;
    }

    @PutMapping
    public ResponseEntity<Void> enable(Authentication authentication) {
        mfaManagementUseCase.enableMfa(authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> disable(Authentication authentication) {
        mfaManagementUseCase.disableMfa(authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
