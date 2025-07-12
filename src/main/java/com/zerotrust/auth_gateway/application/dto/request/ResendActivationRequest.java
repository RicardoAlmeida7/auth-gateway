package com.zerotrust.auth_gateway.application.dto.request;

public record ResendActivationRequest(String email, String username) {
}
