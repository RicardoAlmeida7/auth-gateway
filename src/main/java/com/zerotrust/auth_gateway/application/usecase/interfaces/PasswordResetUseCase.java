package com.zerotrust.auth_gateway.application.usecase.interfaces;

import com.zerotrust.auth_gateway.application.dto.PasswordResetRequest;

public interface PasswordResetUseCase {
    void sendResetLink(String email);
    void resetPassword(String token, PasswordResetRequest request);
}
