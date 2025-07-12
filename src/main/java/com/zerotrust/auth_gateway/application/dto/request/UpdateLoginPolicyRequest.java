package com.zerotrust.auth_gateway.application.dto.request;

public record UpdateLoginPolicyRequest(int maxAttempts, long lockTimeMillis) {
}
