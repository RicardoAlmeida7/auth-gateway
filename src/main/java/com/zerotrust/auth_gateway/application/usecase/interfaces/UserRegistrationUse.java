package com.zerotrust.auth_gateway.application.usecase.interfaces;

import com.zerotrust.auth_gateway.application.dto.request.RegisterRequest;
import com.zerotrust.auth_gateway.application.dto.request.ResendActivationRequest;

public interface UserRegistrationUse {
    void register(RegisterRequest request);
    void resendActivationEmail(ResendActivationRequest request);
}
