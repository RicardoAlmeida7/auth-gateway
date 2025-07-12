package com.zerotrust.auth_gateway.application.dto.request;

public record AuthenticationRequest(String username, String email, String password, String otp) {
}
