package com.zerotrust.auth_gateway.infrastructure.cache;

import com.zerotrust.auth_gateway.domain.service.interfaces.TokenBlacklistService;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

public class RedisTokenBlacklistServiceImpl implements TokenBlacklistService {

    private static final String CACHE_PREFIX = "tokenBlacklist:";

    private final RedisTemplate<String, Boolean> redisTemplate;

    public RedisTokenBlacklistServiceImpl(RedisTemplate<String, Boolean> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void blacklistToken(String tokenHash, long expiresInSeconds) {
        String key = CACHE_PREFIX + tokenHash;
        Duration ttl = Duration.ofSeconds(expiresInSeconds);
        redisTemplate.opsForValue().set(key, Boolean.TRUE, ttl);
    }

    @Override
    public boolean isTokenBlacklisted(String tokenHash) {
        String key = CACHE_PREFIX + tokenHash;
        Boolean hasKey = redisTemplate.opsForValue().get(key);
        return hasKey != null && hasKey;
    }
}
