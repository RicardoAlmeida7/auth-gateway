package com.zerotrust.auth_gateway.web.dto;

import com.zerotrust.auth_gateway.domain.enums.Role;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterRequestTest {

    @Test
    void shouldCreateRegisterRequestWithNullRolesDefaultsToRoleUser() {
        String username = "newUser";
        String password = "newPass";
        String email = "newuser@example.com";

        RegisterRequest request = new RegisterRequest(username, password, email, null);

        assertEquals(username, request.getUsername());
        assertEquals(password, request.getPassword());
        assertEquals(email, request.getEmail());
        assertEquals(List.of(Role.ROLE_USER.name()), request.getRoles()); // default role
    }

    @Test
    void shouldCreateRegisterRequestWithEmptyRolesDefaultsToRoleUser() {
        String username = "emptyRolesUser";
        String password = "pass";
        String email = "empty@example.com";

        RegisterRequest request = new RegisterRequest(username, password, email, List.of());

        assertEquals(List.of(Role.ROLE_USER.name()), request.getRoles()); // default role
    }

    @Test
    void shouldCreateRegisterRequestWithExplicitRoles() {
        String username = "userWithRoles";
        String password = "passWithRoles";
        String email = "roles@example.com";
        List<String> roles = List.of("ROLE_USER", "ROLE_ADMIN");

        RegisterRequest request = new RegisterRequest(username, password, email, roles);

        assertEquals(roles, request.getRoles());
    }

    @Test
    void shouldCreateRegisterRequestWithThreeArgsConstructor() {
        String username = "basicUser";
        String password = "basicPass";
        String email = "basic@example.com";

        RegisterRequest request = new RegisterRequest(username, password, email);

        assertEquals(List.of(Role.ROLE_USER.name()), request.getRoles());
        assertFalse(request.isMfaEnabled());
    }

    @Test
    void shouldCreateRegisterRequestWithMfaEnabled() {
        String username = "mfaUser";
        String password = "mfaPass";
        String email = "mfa@example.com";
        boolean mfaEnabled = true;

        RegisterRequest request = new RegisterRequest(username, password, email, mfaEnabled);

        assertEquals(username, request.getUsername());
        assertEquals(password, request.getPassword());
        assertEquals(email, request.getEmail());
        assertEquals(List.of(Role.ROLE_USER.name()), request.getRoles());
        assertTrue(request.isMfaEnabled());
    }

    @Test
    void shouldAllowFieldMutationViaSetters() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user2");
        request.setPassword("pass2");
        request.setEmail("user2@example.com");
        request.setRoles(List.of("ROLE_USER", "ROLE_ADMIN"));
        request.setMfaEnabled(true);

        assertEquals("user2", request.getUsername());
        assertEquals("pass2", request.getPassword());
        assertEquals("user2@example.com", request.getEmail());
        assertEquals(List.of("ROLE_USER", "ROLE_ADMIN"), request.getRoles());
        assertTrue(request.isMfaEnabled());
    }

    @Test
    void shouldImplementEqualsAndHashCodeCorrectly() {
        RegisterRequest r1 = new RegisterRequest("abc", "123", "abc@example.com");
        RegisterRequest r2 = new RegisterRequest("abc", "123", "abc@example.com");
        RegisterRequest r3 = new RegisterRequest("def", "456", "def@example.com");

        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void shouldReturnMeaningfulToString() {
        RegisterRequest request = new RegisterRequest("userX", "passY", "x@example.com");
        String str = request.toString();

        assertTrue(str.contains("userX"));
        assertTrue(str.contains("passY"));
    }
}
