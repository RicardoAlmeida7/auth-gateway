package com.zerotrust.auth_gateway.application.usecase.interfaces.activation;

import com.zerotrust.auth_gateway.application.dto.request.password.PasswordResetRequest;

public interface AccountActivationUseCase {
    void activate(String token, PasswordResetRequest request);
}
