package com.zerotrust.auth_gateway.application.dto.request.user;

import java.util.List;

public record AdminUpdateUserRequest(String username, String email, List<String> roles) {
}
