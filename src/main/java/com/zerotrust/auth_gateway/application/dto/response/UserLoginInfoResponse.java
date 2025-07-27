package com.zerotrust.auth_gateway.application.dto.response;

public record UserLoginInfoResponse(String userId, boolean mfaEnabled, boolean active) {
}
