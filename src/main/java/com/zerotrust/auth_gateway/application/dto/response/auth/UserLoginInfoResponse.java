package com.zerotrust.auth_gateway.application.dto.response.auth;

public record UserLoginInfoResponse(String userId, boolean mfaEnabled, boolean active) {
}
