package com.zerotrust.auth_gateway.application.usecase;

import com.zerotrust.auth_gateway.web.dto.RegisterRequest;

public interface RegisterUserUseCase {
    void register(RegisterRequest command);
}
