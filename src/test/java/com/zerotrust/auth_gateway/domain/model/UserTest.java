package com.zerotrust.auth_gateway.domain.model;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    void shouldCreateUserSuccessfully() {
        UUID id = UUID.randomUUID();
        List<String> roles = List.of("ROLE_USER");
        User user = new User(id, "username", "hashed123", false, "SECRET", true, roles);

        assertEquals(id, user.getId());
        assertEquals("username", user.getUsername());
        assertEquals("hashed123", user.getPasswordHash());
        assertEquals("SECRET", user.getMfaSecret());
        assertTrue(user.isEnabled());
        assertFalse(user.isMfaEnabled());
        assertEquals(roles, user.getRoles());
    }

    @Test
    void shouldUseEmptyListIfRolesIsNull() {
        User user = new User(UUID.randomUUID(), "user", "pass", false, null, true, null);
        assertNotNull(user.getRoles());
        assertTrue(user.getRoles().isEmpty());
    }

    @Test
    void shouldThrowWhenIdIsNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new User(null, "username", "hashed", false, "secret", true, List.of("ROLE_USER"))
        );
        assertEquals("ID cannot be null.", exception.getMessage());
    }

    @Test
    void shouldThrowWhenUsernameIsNullOrBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                new User(UUID.randomUUID(), null, "hashed", false, "secret", true, List.of("ROLE_USER"))
        );
        assertThrows(IllegalArgumentException.class, () ->
                new User(UUID.randomUUID(), "   ", "hashed", false, "secret", true, List.of("ROLE_USER"))
        );
    }

    @Test
    void shouldThrowWhenPasswordHashIsNullOrBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                new User(UUID.randomUUID(), "username", null, false, "secret", true, List.of("ROLE_USER"))
        );
        assertThrows(IllegalArgumentException.class, () ->
                new User(UUID.randomUUID(), "username", "", false, "secret", true, List.of("ROLE_USER"))
        );
    }

    @Test
    void shouldConsiderUsersEqualById() {
        UUID id = UUID.randomUUID();
        User user1 = new User(id, "user1", "pass1", false, "sec1", true, List.of("ROLE_USER"));
        User user2 = new User(id, "user2", "pass2", true, "sec2", false, List.of("ROLE_ADMIN"));

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void shouldAllowChangingFieldsViaSetters() {
        UUID id = UUID.randomUUID();
        User user = new User(id, "username", "pass", false, "secret", true, List.of("ROLE_USER"));

        user.setUsername("username2");
        user.setPasswordHash("newpass");
        user.setMfaEnabled(true);
        user.setEnabled(false);
        user.setMfaSecret("newsecret");
        user.setRoles(Collections.singletonList("ROLE_ADMIN"));

        assertEquals("username2", user.getUsername());
        assertEquals("newpass", user.getPasswordHash());
        assertTrue(user.isMfaEnabled());
        assertEquals("newsecret", user.getMfaSecret());
        assertFalse(user.isEnabled());
        assertEquals(List.of("ROLE_ADMIN"), user.getRoles());
    }

    @Test
    void testToStringContainsUsernameAndRoles() {
        User user = new User(UUID.randomUUID(), "username", "pass", false, null, true, List.of("ROLE_USER"));
        String output = user.toString();
        assertTrue(output.contains("username"));
        assertTrue(output.contains("ROLE_USER"));
    }
}
