package com.zerotrust.auth_gateway.application.port.in;

import com.zerotrust.auth_gateway.web.dto.RegisterUserCommand;

public interface RegisterUserUseCase {
    void register(RegisterUserCommand command);
}
