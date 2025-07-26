package com.zerotrust.auth_gateway.infrastructure.web.controller;

import com.zerotrust.auth_gateway.application.dto.request.PasswordResetRequest;
import com.zerotrust.auth_gateway.application.dto.request.RegisterRequest;
import com.zerotrust.auth_gateway.application.dto.request.ResendActivationRequest;
import com.zerotrust.auth_gateway.application.usecase.interfaces.ActivateAccountUseCase;
import com.zerotrust.auth_gateway.application.usecase.interfaces.UserRegistrationUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserRegistrationUseCase userRegistrationUseCase;
    private final ActivateAccountUseCase activateAccountUseCase;

    public UserController(
            UserRegistrationUseCase userRegistrationUseCase,
            ActivateAccountUseCase activateAccountUseCase
    ) {
        this.userRegistrationUseCase = userRegistrationUseCase;
        this.activateAccountUseCase = activateAccountUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
        userRegistrationUseCase.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/activate")
    public ResponseEntity<Void> activateAccount(@RequestParam String token, @RequestBody(required = false) PasswordResetRequest request) {
        activateAccountUseCase.activate(token, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resend-activation-email")
    public ResponseEntity<Void> resendActivation(@RequestBody ResendActivationRequest request) {
        userRegistrationUseCase.resendActivationEmail(request);
        return ResponseEntity.ok().build();
    }
}
