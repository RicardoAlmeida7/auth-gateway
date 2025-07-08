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
        boolean firstAccessRequired = false;
        User user = new User(id, "username", "hashed123", "user@example.com", false, "SECRET", true, roles, firstAccessRequired);

        assertEquals(id, user.getId());
        assertEquals("username", user.getUsername());
        assertEquals("hashed123", user.getPasswordHash());
        assertEquals("user@example.com", user.getEmail());
        assertEquals("SECRET", user.getMfaSecret());
        assertTrue(user.isEnabled());
        assertFalse(user.isMfaEnabled());
        assertEquals(roles, user.getRoles());
        assertFalse(user.isFirstAccessRequired());
    }

    @Test
    void shouldUseEmptyListIfRolesIsNull() {
        User user = new User(UUID.randomUUID(), "user", "pass", "mail@mail.com", false, null, true, null, false);
        assertNotNull(user.getRoles());
        assertTrue(user.getRoles().isEmpty());
    }

    @Test
    void shouldThrowWhenIdIsNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new User(null, "username", "hashed", "mail@mail.com", false, "secret", true, List.of("ROLE_USER"), false)
        );
        assertEquals("ID cannot be null.", exception.getMessage());
    }

    @Test
    void shouldConsiderUsersEqualById() {
        UUID id = UUID.randomUUID();
        User user1 = new User(id, "user1", "pass1", "email1@mail.com", false, "sec1", true, List.of("ROLE_USER"), false);
        User user2 = new User(id, "user2", "pass2", "email2@mail.com", true, "sec2", false, List.of("ROLE_ADMIN"), true);

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void shouldAllowChangingFieldsViaSetters() {
        UUID id = UUID.randomUUID();
        User user = new User(id, "username", "pass", "original@email.com", false, "secret", true, List.of("ROLE_USER"), false);

        user.setUsername("username2");
        user.setPasswordHash("newpass");
        user.setMfaEnabled(true);
        user.setEnabled(false);
        user.setMfaSecret("newsecret");
        user.setRoles(Collections.singletonList("ROLE_ADMIN"));
        user.setEmail("updated@email.com");
        user.setFirstAccessRequired(true);

        assertEquals("username2", user.getUsername());
        assertEquals("newpass", user.getPasswordHash());
        assertTrue(user.isMfaEnabled());
        assertEquals("newsecret", user.getMfaSecret());
        assertFalse(user.isEnabled());
        assertEquals(List.of("ROLE_ADMIN"), user.getRoles());
        assertEquals("updated@email.com", user.getEmail());
        assertTrue(user.isFirstAccessRequired());
    }

    @Test
    void testToStringContainsUsernameAndRoles() {
        User user = new User(UUID.randomUUID(), "username", "pass", "email@example.com", false, null, true, List.of("ROLE_USER"), false);
        String output = user.toString();
        assertTrue(output.contains("username"));
        assertTrue(output.contains("ROLE_USER"));
    }
}
