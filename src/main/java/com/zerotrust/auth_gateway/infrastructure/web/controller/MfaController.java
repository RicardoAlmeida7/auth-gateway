package com.zerotrust.auth_gateway.infrastructure.web.controller;

import com.zerotrust.auth_gateway.application.usecase.interfaces.MfaManagementUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user/mfa")
public class MfaController {

    private final MfaManagementUseCase mfaManagementUseCase;

    public MfaController(MfaManagementUseCase mfaManagementUseCase) {
        this.mfaManagementUseCase = mfaManagementUseCase;
    }

    @PostMapping("/enable")
    public ResponseEntity<Void> enable(Authentication authentication) {
        mfaManagementUseCase.enableMfa(authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/disable")
    public ResponseEntity<Void> disable(Authentication authentication) {
        mfaManagementUseCase.disableMfa(authentication.getName());
        return ResponseEntity.ok().build();
    }
}
