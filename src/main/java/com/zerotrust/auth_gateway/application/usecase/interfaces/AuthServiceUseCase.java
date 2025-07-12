package com.zerotrust.auth_gateway.application.usecase.interfaces;

import com.zerotrust.auth_gateway.application.dto.AuthenticationRequest;

public interface AuthServiceUseCase {
    String login(AuthenticationRequest request);
}
