package com.zerotrust.auth_gateway.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    @Test
    void shouldCreateUserSuccessfully() {
        UUID id = UUID.randomUUID();
        User user = new User(id, "username", "hashed123", false, "SECRET", true);

        assertEquals(id, user.getId());
        assertEquals("username", user.getUsername());
        assertEquals("hashed123", user.getPasswordHash());
        assertEquals("SECRET", user.getMfaSecret());
        assertTrue(user.isEnabled());
        assertFalse(user.isMfaEnabled());
    }

    @Test
    void shouldThrowWhenIdIsNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new User(null, "username", "hashed", false, "secret", true)
        );
        assertEquals("ID cannot be null.", exception.getMessage());
    }

    @Test
    void shouldThrowWhenUsernameIsNullOrBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                new User(UUID.randomUUID(), null, "hashed", false, "secret", true)
        );
        assertThrows(IllegalArgumentException.class, () ->
                new User(UUID.randomUUID(), "   ", "hashed", false, "secret", true)
        );
    }

    @Test
    void shouldThrowWhenPasswordHashIsNullOrBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                new User(UUID.randomUUID(), "username", null, false, "secret", true)
        );
        assertThrows(IllegalArgumentException.class, () ->
                new User(UUID.randomUUID(), "username", "", false, "secret", true)
        );
    }

    @Test
    void shouldConsiderUsersEqualById() {
        UUID id = UUID.randomUUID();
        User user1 = new User(id, "user1", "pass1", false, "sec1", true);
        User user2 = new User(id, "user2", "pass2", true, "sec2", false);

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void shouldAllowChangingFieldsViaSetters() {
        UUID id = UUID.randomUUID();
        User user = new User(id, "username", "pass", false, "secret", true);

        user.setUsername("username2");
        user.setPasswordHash("newpass");
        user.setMfaEnabled(true);
        user.setEnabled(false);
        user.setMfaSecret("newsecret");

        assertEquals("username2", user.getUsername());
        assertEquals("newpass", user.getPasswordHash());
        assertTrue(user.isMfaEnabled());
        assertEquals("newsecret", user.getMfaSecret());
        assertFalse(user.isEnabled());
    }
}
