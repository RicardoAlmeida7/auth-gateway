package com.zerotrust.auth_gateway.application.dto.response;

public record LoginPolicyResponse(int maxAttempts, long lockTimeMillis) {
}
