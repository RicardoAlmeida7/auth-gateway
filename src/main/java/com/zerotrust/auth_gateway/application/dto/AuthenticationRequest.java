package com.zerotrust.auth_gateway.application.dto;

public record AuthenticationRequest(String username, String email, String password, String otp) {
}
