package com.zerotrust.auth_gateway.web.controller;

import com.zerotrust.auth_gateway.application.usecase.interfaces.ActivateAccountUseCase;
import com.zerotrust.auth_gateway.application.usecase.interfaces.UserServiceUseCase;
import com.zerotrust.auth_gateway.web.dto.RegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserServiceUseCase useServiceUseCase;
    private final ActivateAccountUseCase activateAccountUseCase;

    public UserController(UserServiceUseCase useServiceUseCase, ActivateAccountUseCase activateAccountUseCase) {
        this.useServiceUseCase = useServiceUseCase;
        this.activateAccountUseCase = activateAccountUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
        useServiceUseCase.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/activate")
    public ResponseEntity<Void> activateAccount(@RequestParam String token) {
        activateAccountUseCase.activate(token);
        return ResponseEntity.ok().build();
    }
}
