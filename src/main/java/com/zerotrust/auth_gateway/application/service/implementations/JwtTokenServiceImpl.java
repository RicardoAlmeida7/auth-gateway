package com.zerotrust.auth_gateway.application.service.implementations;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.zerotrust.auth_gateway.application.dto.response.auth.JwtResponse;
import com.zerotrust.auth_gateway.application.service.interfaces.JwtTokenService;
import com.zerotrust.auth_gateway.domain.exception.InvalidTokenException;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.domain.service.TokenBlacklistService;
import com.zerotrust.auth_gateway.domain.utils.Constants;
import com.zerotrust.auth_gateway.domain.utils.HashUtil;
import com.zerotrust.auth_gateway.infrastructure.security.jwt.JwtTokenGenerator;

public class JwtTokenServiceImpl implements JwtTokenService {

    private final JwtTokenGenerator jwtTokenGenerator;
    private final TokenBlacklistService tokenBlacklistService;

    public JwtTokenServiceImpl(JwtTokenGenerator jwtTokenGenerator, TokenBlacklistService tokenBlacklistService) {
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    public JwtResponse generateAuthToken(User user) {
        String accessToken = jwtTokenGenerator.generateToken(user.getUsername(), user.getRoles());
        String refreshToken = jwtTokenGenerator.generateRefreshToken(user.getUsername());
        return new JwtResponse(accessToken, refreshToken);
    }

    @Override
    public String generateActivationToken(User user) {
        return jwtTokenGenerator.generateActivationToken(user.getUsername(), user.getEmail());
    }

    @Override
    public String generateResetPasswordToken(User user) {
        return jwtTokenGenerator.generateResetPasswordToken(user.getUsername(), user.getEmail());
    }

    @Override
    public void blacklistAuthTokens(String accessToken, String refreshToken) {
        String hashAccessToken = HashUtil.sha256(accessToken);
        String hashRefreshToken = HashUtil.sha256(refreshToken);
        tokenBlacklistService.blacklistToken(hashAccessToken, Constants.AUTHENTICATION_TOKEN_TTL_SECONDS);
        tokenBlacklistService.blacklistToken(hashRefreshToken, Constants.REFRESH_TOKEN_TTL_SECONDS);
    }

    @Override
    public void blacklistActivationToken(String activationToken) {
        String hashActivation = HashUtil.sha256(activationToken);
        tokenBlacklistService.blacklistToken(hashActivation, Constants.ACTIVATION_TOKEN_TTL_SECONDS);
    }

    @Override
    public void blacklistResetPasswordToken(String resetPasswordToken) {
        String hashResetPassword = HashUtil.sha256(resetPasswordToken);
        tokenBlacklistService.blacklistToken(hashResetPassword, Constants.ACTIVATION_TOKEN_TTL_SECONDS);
    }

    @Override
    public String validateActivationToken(String token) {
        DecodedJWT decodedJWT = jwtTokenGenerator.verifyActivationToken(token);
        return verifyTokenNotRevoked(token, decodedJWT);
    }

    @Override
    public String validateRefreshToken(String token) {
        DecodedJWT decodedJWT = jwtTokenGenerator.verifyRefreshToken(token);
        return verifyTokenNotRevoked(token, decodedJWT);
    }

    @Override
    public String validateResetPasswordToken(String token) {
        DecodedJWT decodedJWT = jwtTokenGenerator.verifyResetPasswordToken(token);
        return verifyTokenNotRevoked(token, decodedJWT);
    }

    private String verifyTokenNotRevoked(String token, DecodedJWT decodedJWT) {
        String tokenHash = HashUtil.sha256(token);
        if (tokenBlacklistService.isTokenBlacklisted(tokenHash)) {
            throw new InvalidTokenException("Token revoked.");
        }

        return decodedJWT.getSubject();
    }
}
