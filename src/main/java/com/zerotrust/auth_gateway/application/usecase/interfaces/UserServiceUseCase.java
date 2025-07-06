package com.zerotrust.auth_gateway.application.usecase.interfaces;

import com.zerotrust.auth_gateway.web.dto.RegisterRequest;

public interface UserServiceUseCase {
    void register(RegisterRequest command);
}
