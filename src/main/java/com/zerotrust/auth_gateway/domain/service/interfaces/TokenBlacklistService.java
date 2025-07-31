package com.zerotrust.auth_gateway.domain.service.interfaces;

public interface TokenBlacklistService {

    void blacklistToken(String tokenHash, long expiresInSeconds);
    boolean isTokenBlacklisted(String tokenHash);
}
