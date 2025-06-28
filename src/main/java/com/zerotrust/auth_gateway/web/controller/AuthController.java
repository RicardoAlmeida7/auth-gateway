package com.zerotrust.auth_gateway.web.controller;

import com.zerotrust.auth_gateway.application.port.in.RegisterUserUseCase;
import com.zerotrust.auth_gateway.application.service.RegisterUserService;
import com.zerotrust.auth_gateway.web.dto.RegisterRequest;
import com.zerotrust.auth_gateway.web.dto.RegisterUserCommand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final RegisterUserUseCase registerUserUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
        registerUserUseCase.register(new RegisterUserCommand(request.getUsername(), request.getPassword()));
        return ResponseEntity.ok().build();
    }
}
