package com.zerotrust.auth_gateway.infrastructure.web.controller;

import com.zerotrust.auth_gateway.application.dto.request.password.PasswordResetRequest;
import com.zerotrust.auth_gateway.application.dto.request.registration.RegisterRequest;
import com.zerotrust.auth_gateway.application.dto.request.registration.ResendActivationRequest;
import com.zerotrust.auth_gateway.application.usecase.interfaces.activation.AccountActivationUseCase;
import com.zerotrust.auth_gateway.application.usecase.interfaces.registration.PublicRegistrationUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final PublicRegistrationUseCase publicRegistrationUseCase;
    private final AccountActivationUseCase accountActivationUseCase;

    public UserController(
            PublicRegistrationUseCase publicRegistrationUseCase,
            AccountActivationUseCase accountActivationUseCase
    ) {
        this.publicRegistrationUseCase = publicRegistrationUseCase;
        this.accountActivationUseCase = accountActivationUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
        publicRegistrationUseCase.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/activate")
    public ResponseEntity<Void> activateAccount(@RequestParam String token, @RequestBody(required = false) PasswordResetRequest request) {
        accountActivationUseCase.activate(token, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resend-activation-email")
    public ResponseEntity<Void> resendActivation(@RequestBody ResendActivationRequest request) {
        publicRegistrationUseCase.resendActivationEmail(request);
        return ResponseEntity.ok().build();
    }
}
