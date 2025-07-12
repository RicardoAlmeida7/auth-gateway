package com.zerotrust.auth_gateway.application.usecase.interfaces;

import com.zerotrust.auth_gateway.application.dto.request.PasswordResetRequest;

public interface ActivateAccountUseCase {
    void activate(String token, PasswordResetRequest request);
}
