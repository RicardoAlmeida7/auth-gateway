package com.zerotrust.auth_gateway.application.dto.response.user;

import java.util.UUID;

public record UserResponse(UUID id, String username, String email, boolean active) {
}
