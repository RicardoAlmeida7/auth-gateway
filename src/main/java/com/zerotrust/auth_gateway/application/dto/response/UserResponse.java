package com.zerotrust.auth_gateway.application.dto.response;

import java.util.UUID;

public record UserResponse(UUID id, String username, String email, boolean active) {
}
