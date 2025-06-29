package com.zerotrust.auth_gateway.infrastructure.repository.entities;

import com.zerotrust.auth_gateway.infrastructure.repository.entities.UserEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserEntityTest {

    @Test
    void testUserEntityGettersAndSetters() {
        UUID id = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setUsername("testuser");
        user.setPasswordHash("hashedPass");
        user.setMfaEnabled(true);
        user.setMfaSecret("secret");
        user.setEnabled(true);

        assertEquals(id, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("hashedPass", user.getPasswordHash());
        assertTrue(user.isMfaEnabled());
        assertEquals("secret", user.getMfaSecret());
        assertTrue(user.isEnabled());
    }

    @Test
    void testUserEntityConstructor() {
        UUID id = UUID.randomUUID();
        UserEntity user = new UserEntity(id, "username", "hash", false, "mfaSecret", true);

        assertEquals(id, user.getId());
        assertEquals("username", user.getUsername());
        assertEquals("hash", user.getPasswordHash());
        assertFalse(user.isMfaEnabled());
        assertEquals("mfaSecret", user.getMfaSecret());
        assertTrue(user.isEnabled());
    }

    @Test
    void testEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        UserEntity user1 = new UserEntity(id, "user1", "hash1", false, "", true);
        UserEntity user2 = new UserEntity(id, "user2", "hash2", true, "secret", false);

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }
}
