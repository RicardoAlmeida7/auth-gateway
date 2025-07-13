package com.zerotrust.auth_gateway.application.dto.response;

public record JwtResponse(String accessToken, String refreshToken) {
}
