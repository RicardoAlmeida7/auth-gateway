package com.zerotrust.auth_gateway.application.dto.response.policy;

public record LoginPolicyResponse(int maxAttempts, long lockTimeMillis) {
}
