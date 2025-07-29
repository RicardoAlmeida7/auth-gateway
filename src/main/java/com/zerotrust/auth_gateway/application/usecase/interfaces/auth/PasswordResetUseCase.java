package com.zerotrust.auth_gateway.application.usecase.interfaces.auth;

import com.zerotrust.auth_gateway.application.dto.request.password.PasswordResetEmailRequest;
import com.zerotrust.auth_gateway.application.dto.request.password.PasswordResetRequest;

public interface PasswordResetUseCase {
    void sendResetLink(PasswordResetEmailRequest request);
    void resetPassword(String token, PasswordResetRequest request);
}
