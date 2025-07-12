package com.zerotrust.auth_gateway.application.dto.request;

public record AuthenticationRequest(String userId, String password, String otp) {
}
