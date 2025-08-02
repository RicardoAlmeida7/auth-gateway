package com.zerotrust.auth_gateway.infrastructure.web.controller;

import com.zerotrust.auth_gateway.application.dto.request.policy.UpdateLoginPolicyRequest;
import com.zerotrust.auth_gateway.application.dto.response.policy.LoginPolicyResponse;
import com.zerotrust.auth_gateway.application.usecase.interfaces.auth.LoginPolicyUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/policy")
public class LoginPolicyController {

    private final LoginPolicyUseCase loginPolicyUseCase;

    public LoginPolicyController(LoginPolicyUseCase loginPolicyUseCase) {
        this.loginPolicyUseCase = loginPolicyUseCase;
    }

    @GetMapping
    public ResponseEntity<LoginPolicyResponse> getPolicy() {
        return ResponseEntity.ok(loginPolicyUseCase.getPolicy());
    }

    @PutMapping
    public ResponseEntity<Void> updatePolicy(@RequestBody(required = false)UpdateLoginPolicyRequest request) {
        loginPolicyUseCase.updatePolicy(request);
        return ResponseEntity.ok().build();
    }
}
