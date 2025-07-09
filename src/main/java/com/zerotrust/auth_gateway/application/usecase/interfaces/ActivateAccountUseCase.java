package com.zerotrust.auth_gateway.application.usecase.interfaces;

import com.zerotrust.auth_gateway.infrastructure.web.dto.PasswordResetRequest;

public interface ActivateAccountUseCase {
    void activate(String token, PasswordResetRequest request);
}
