package com.zerotrust.auth_gateway.application.usecase.interfaces.auth;

import com.zerotrust.auth_gateway.application.dto.request.auth.AuthenticationRequest;
import com.zerotrust.auth_gateway.application.dto.response.auth.JwtResponse;
import com.zerotrust.auth_gateway.application.dto.response.auth.UserLoginInfoResponse;

public interface AuthenticationUseCase {
    JwtResponse login(AuthenticationRequest request);
    JwtResponse refreshToken(String refreshToken);
    UserLoginInfoResponse getUserStatusInfo(String userId);
    void logout(String accessTokenHeader, String refreshToken);
}
