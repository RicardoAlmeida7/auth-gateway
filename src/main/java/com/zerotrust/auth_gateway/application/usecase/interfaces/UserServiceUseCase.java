package com.zerotrust.auth_gateway.application.usecase.interfaces;

import com.zerotrust.auth_gateway.application.dto.RegisterRequest;
import com.zerotrust.auth_gateway.application.dto.ResendActivationRequest;

public interface UserServiceUseCase {
    void register(RegisterRequest request);
    void resendActivationEmail(ResendActivationRequest request);
}
