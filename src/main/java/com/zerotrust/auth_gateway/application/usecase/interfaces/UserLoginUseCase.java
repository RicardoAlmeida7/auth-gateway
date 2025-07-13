package com.zerotrust.auth_gateway.application.usecase.interfaces;

import com.zerotrust.auth_gateway.application.dto.request.AuthenticationRequest;
import com.zerotrust.auth_gateway.application.dto.request.RefreshTokenRequest;
import com.zerotrust.auth_gateway.application.dto.response.JwtResponse;

public interface UserLoginUseCase {
    JwtResponse login(AuthenticationRequest request);
    JwtResponse refreshToken(RefreshTokenRequest request);
}
