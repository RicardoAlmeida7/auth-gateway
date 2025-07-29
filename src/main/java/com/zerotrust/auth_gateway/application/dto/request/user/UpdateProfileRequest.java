package com.zerotrust.auth_gateway.application.dto.request.user;

public record UpdateProfileRequest(String username, String email, String newPassword, String confirmPassword) {
}
