package com.zerotrust.auth_gateway.application.usecase.interfaces;

import com.zerotrust.auth_gateway.application.dto.request.PasswordResetEmailRequest;
import com.zerotrust.auth_gateway.application.dto.request.PasswordResetRequest;

public interface PasswordResetUseCase {
    void sendResetLink(PasswordResetEmailRequest request);
    void resetPassword(String token, PasswordResetRequest request);
}
