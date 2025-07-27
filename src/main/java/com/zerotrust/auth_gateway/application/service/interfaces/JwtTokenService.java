package com.zerotrust.auth_gateway.application.service.interfaces;

import com.zerotrust.auth_gateway.application.dto.response.JwtResponse;
import com.zerotrust.auth_gateway.domain.model.User;

public interface JwtTokenService {

    JwtResponse generateAuthToken(User user);
    String generateActivationToken(User user);
    String generateResetPasswordToken(User user);

    void blacklistAuthTokens(String accessToken, String refreshToken);
    void blacklistActivationToken(String activationToken);
    void blacklistResetPasswordToken(String resetPasswordToken);

    String validateActivationToken(String token);
    String validateRefreshToken(String token);
    String validateResetPasswordToken(String token);
}
