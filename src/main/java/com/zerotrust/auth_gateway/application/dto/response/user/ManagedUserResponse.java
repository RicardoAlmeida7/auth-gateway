package com.zerotrust.auth_gateway.application.dto.response.user;

import java.util.List;
import java.util.UUID;

public record ManagedUserResponse(
        UUID id,
        String username,
        String email,
        boolean active,
        boolean blockedByAdmin,
        boolean mfaEnabled,
        List<String> roles
) {
}
