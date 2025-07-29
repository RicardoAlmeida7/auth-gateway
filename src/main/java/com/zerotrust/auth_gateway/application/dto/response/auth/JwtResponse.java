package com.zerotrust.auth_gateway.application.dto.response.auth;

public record JwtResponse(String accessToken, String refreshToken) {
}
