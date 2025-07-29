package com.zerotrust.auth_gateway.application.usecase.interfaces.registration;

import com.zerotrust.auth_gateway.application.dto.request.registration.RegisterRequest;
import com.zerotrust.auth_gateway.application.dto.request.registration.ResendActivationRequest;

public interface PublicRegistrationUseCase {
    void register(RegisterRequest request);
    void resendActivationEmail(ResendActivationRequest request);
}
