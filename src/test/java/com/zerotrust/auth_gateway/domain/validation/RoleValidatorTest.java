package com.zerotrust.auth_gateway.domain.validation;

import com.zerotrust.auth_gateway.domain.exception.InvalidRoleException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RoleValidatorTest {

    @Test
    void shouldPassWithValidRoles() {
        assertDoesNotThrow(() -> RoleValidator.validate(List.of("ROLE_USER", "ROLE_ADMIN")));
    }

    @Test
    void shouldFailWhenRoleIsInvalid() {
        InvalidRoleException ex = assertThrows(InvalidRoleException.class,
                () -> RoleValidator.validate(List.of("INVALID_ROLE")));
        assertTrue(ex.getMessage().contains("Valid roles"));
    }

    @Test
    void shouldFailWhenRolesIsNull() {
        InvalidRoleException ex = assertThrows(InvalidRoleException.class,
                () -> RoleValidator.validate(null));
        assertTrue(ex.getMessage().contains("At least one role"));
    }

    @Test
    void shouldFailWhenRolesIsEmpty() {
        InvalidRoleException ex = assertThrows(InvalidRoleException.class,
                () -> RoleValidator.validate(List.of()));
        assertTrue(ex.getMessage().contains("At least one role"));
    }
}
