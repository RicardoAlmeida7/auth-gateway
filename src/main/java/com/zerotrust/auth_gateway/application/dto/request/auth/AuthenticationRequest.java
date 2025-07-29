package com.zerotrust.auth_gateway.application.dto.request.auth;

public record AuthenticationRequest(String userId, String password, String otp) {
}
