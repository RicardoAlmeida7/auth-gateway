package com.zerotrust.auth_gateway.infrastructure.web.controller;

import com.zerotrust.auth_gateway.application.dto.request.password.PasswordResetRequest;
import com.zerotrust.auth_gateway.application.dto.request.registration.RegisterRequest;
import com.zerotrust.auth_gateway.application.dto.request.registration.ResendActivationRequest;
import com.zerotrust.auth_gateway.application.dto.request.user.UpdateProfileRequest;
import com.zerotrust.auth_gateway.application.usecase.interfaces.activation.AccountActivationUseCase;
import com.zerotrust.auth_gateway.application.usecase.interfaces.registration.PublicRegistrationUseCase;
import com.zerotrust.auth_gateway.application.usecase.interfaces.user.UserProfileUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final PublicRegistrationUseCase publicRegistrationUseCase;
    private final AccountActivationUseCase accountActivationUseCase;
    private final UserProfileUseCase userProfileUseCase;

    public UserController(
            PublicRegistrationUseCase publicRegistrationUseCase,
            AccountActivationUseCase accountActivationUseCase,
            UserProfileUseCase userProfileUseCase
    ) {
        this.publicRegistrationUseCase = publicRegistrationUseCase;
        this.accountActivationUseCase = accountActivationUseCase;
        this.userProfileUseCase = userProfileUseCase;
    }

    @PostMapping
    public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
        publicRegistrationUseCase.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/activation")
    public ResponseEntity<Void> activateAccount(@RequestParam String token, @RequestBody(required = false) PasswordResetRequest request) {
        accountActivationUseCase.activate(token, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/activation/resend")
    public ResponseEntity<Void> resendActivation(@RequestBody ResendActivationRequest request) {
        publicRegistrationUseCase.resendActivationEmail(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/me")
    public ResponseEntity<Void> updateProfile(@RequestBody UpdateProfileRequest request, Authentication authentication) {
        userProfileUseCase.updateProfile(authentication.getName(), request);
        return ResponseEntity.noContent().build();
    }
}
