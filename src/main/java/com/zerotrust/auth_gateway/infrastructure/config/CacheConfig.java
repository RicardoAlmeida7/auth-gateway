package com.zerotrust.auth_gateway.infrastructure.config;

import com.zerotrust.auth_gateway.domain.service.TokenBlacklistService;
import com.zerotrust.auth_gateway.infrastructure.cache.RedisTokenBlacklistServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class CacheConfig {

    @Bean
    public TokenBlacklistService tokenBlacklistService(RedisTemplate<String, Boolean> redisTemplate) {
        return new RedisTokenBlacklistServiceImpl(redisTemplate);
    }
}
