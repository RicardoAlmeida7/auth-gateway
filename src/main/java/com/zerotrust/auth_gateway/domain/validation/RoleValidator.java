package com.zerotrust.auth_gateway.domain.validation;

import com.zerotrust.auth_gateway.domain.enums.Role;
import com.zerotrust.auth_gateway.domain.exception.InvalidRoleException;

import java.util.Arrays;
import java.util.List;

public class RoleValidator {

    private RoleValidator() {
    }

    public static void validate(List<String> roles) {
        List<String> validRoles = Arrays.stream(Role.values())
                .map(Enum::name)
                .toList();

        if (roles == null || roles.isEmpty()) {
            throw new InvalidRoleException("At least one role must be provided. Valid roles: " + validRoles);
        }

        for (String role : roles) {
            if (!validRoles.contains(role)) {
                throw new InvalidRoleException("At least one role must be provided. Valid roles: " + validRoles);
            }
        }
    }
}
