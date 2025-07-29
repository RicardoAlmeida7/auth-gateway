package com.zerotrust.auth_gateway.application.dto.request.policy;

public record UpdateLoginPolicyRequest(int maxAttempts, long lockTimeMillis) {
}
