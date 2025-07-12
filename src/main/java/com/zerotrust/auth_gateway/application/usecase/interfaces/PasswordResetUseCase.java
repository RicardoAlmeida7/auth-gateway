package com.zerotrust.auth_gateway.application.usecase.interfaces;

import com.zerotrust.auth_gateway.application.dto.PasswordResetEmailRequest;
import com.zerotrust.auth_gateway.application.dto.PasswordResetRequest;

public interface PasswordResetUseCase {
    void sendResetLink(PasswordResetEmailRequest request);
    void resetPassword(String token, PasswordResetRequest request);
}
