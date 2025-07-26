package com.zerotrust.auth_gateway.application.dto.response;

import java.util.List;
import java.util.UUID;

public record AdminUserListResponse(UUID id, String username, String email, boolean active, List<String> roles) {
}
