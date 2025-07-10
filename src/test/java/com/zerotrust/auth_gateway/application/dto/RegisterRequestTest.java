package com.zerotrust.auth_gateway.application.dto;

import com.zerotrust.auth_gateway.domain.enums.Role;
import com.zerotrust.auth_gateway.application.dto.RegisterRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterRequestTest {

    @Test
    void shouldDefaultRolesToRoleUserWhenNull() {
        RegisterRequest request = new RegisterRequest("user", "pass", "email@test.com", null, false, "pass", false);
        assertEquals(List.of(Role.ROLE_USER.name()), request.getRoles());
    }

    @Test
    void shouldDefaultRolesToRoleUserWhenEmpty() {
        RegisterRequest request = new RegisterRequest("user", "pass", "email@test.com", List.of(), false, "pass", false);
        assertEquals(List.of(Role.ROLE_USER.name()), request.getRoles());
    }

    @Test
    void shouldUseProvidedRoles() {
        List<String> roles = List.of("ROLE_ADMIN", "ROLE_USER");
        RegisterRequest request = new RegisterRequest("user", "pass", "email@test.com", roles, false, "pass", false);
        assertEquals(roles, request.getRoles());
    }

    @Test
    void shouldSetMfaEnabledAndFirstAccessRequired() {
        RegisterRequest request = new RegisterRequest("user", "pass", "email@test.com", List.of(Role.ROLE_USER.name()), true, "pass", true);
        assertTrue(request.isMfaEnabled());
        assertTrue(request.isFirstAccessRequired());
        assertEquals("pass", request.getConfirmPassword());
    }

    @Test
    void shouldAllowUsingOtherConstructors() {
        // Constructor with roles, confirmPassword, firstAccessRequired, MFA disabled
        RegisterRequest r1 = new RegisterRequest("user", "pass", "email@test.com", List.of("ROLE_ADMIN"), "pass", true);
        assertFalse(r1.isMfaEnabled());
        assertTrue(r1.isFirstAccessRequired());
        assertEquals(List.of("ROLE_ADMIN"), r1.getRoles());

        // Constructor with MFA enabled, default role, confirmPassword, firstAccessRequired
        RegisterRequest r2 = new RegisterRequest("user", "pass", "email@test.com", true, "pass", false);
        assertTrue(r2.isMfaEnabled());
        assertFalse(r2.isFirstAccessRequired());
        assertEquals(List.of(Role.ROLE_USER.name()), r2.getRoles());

        // Constructor with confirmPassword and firstAccessRequired only
        RegisterRequest r3 = new RegisterRequest("user", "pass", "email@test.com", "pass", false);
        assertFalse(r3.isMfaEnabled());
        assertFalse(r3.isFirstAccessRequired());
        assertEquals(List.of(Role.ROLE_USER.name()), r3.getRoles());
    }

    @Test
    void shouldGettersAndSettersWork() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user");
        request.setPassword("pass");
        request.setEmail("email@test.com");
        request.setRoles(List.of("ROLE_ADMIN"));
        request.setMfaEnabled(true);
        request.setConfirmPassword("pass");
        request.setFirstAccessRequired(true);

        assertEquals("user", request.getUsername());
        assertEquals("pass", request.getPassword());
        assertEquals("email@test.com", request.getEmail());
        assertEquals(List.of("ROLE_ADMIN"), request.getRoles());
        assertTrue(request.isMfaEnabled());
        assertEquals("pass", request.getConfirmPassword());
        assertTrue(request.isFirstAccessRequired());
    }

    @Test
    void shouldEqualsAndHashCodeBasedOnUsernameAndPassword() {
        RegisterRequest r1 = new RegisterRequest("user", "pass", "email1@test.com", List.of(Role.ROLE_USER.name()), false, "pass", false);
        RegisterRequest r2 = new RegisterRequest("user", "pass", "email2@test.com", List.of(Role.ROLE_ADMIN.name()), true, "pass", true);
        RegisterRequest r3 = new RegisterRequest("user2", "pass", "email1@test.com", null, false, "pass", false);

        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void shouldToStringContainUsernameAndPassword() {
        RegisterRequest r = new RegisterRequest("user", "pass", "email@test.com", null, false, "pass", false);
        String str = r.toString();
        assertTrue(str.contains("user"));
        assertTrue(str.contains("pass"));
    }
}
