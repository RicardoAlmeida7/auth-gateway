package com.zerotrust.auth_gateway.infrastructure.repository.entities;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserEntityTest {

    @Test
    void testUserEntityGettersAndSetters() {
        UUID id = UUID.randomUUID();
        RoleEntity role = new RoleEntity(UUID.randomUUID(), "ROLE_USER");

        UserEntity user = new UserEntity();
        user.setId(id);
        user.setUsername("testuser");
        user.setPasswordHash("hashedPass");
        user.setEmail("test@example.com");
        user.setMfaEnabled(true);
        user.setMfaSecret("secret");
        user.setEnabled(true);
        user.setFirstAccessRequired(false);
        user.setRoles(List.of(role));

        assertEquals(id, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("hashedPass", user.getPasswordHash());
        assertEquals("test@example.com", user.getEmail());
        assertTrue(user.isMfaEnabled());
        assertEquals("secret", user.getMfaSecret());
        assertTrue(user.isEnabled());
        assertFalse(user.isFirstAccessRequired());
        assertNotNull(user.getRoles());
        assertEquals(1, user.getRoles().size());
        assertEquals("ROLE_USER", user.getRoles().get(0).getName());
    }

    @Test
    void testUserEntityConstructor() {
        UUID id = UUID.randomUUID();
        RoleEntity role = new RoleEntity(UUID.randomUUID(), "ROLE_ADMIN");

        UserEntity user = new UserEntity(
                id,
                "username",
                "hash",
                "user@example.com",
                false,
                "mfaSecret",
                true,
                true,
                List.of(role)
        );

        assertEquals(id, user.getId());
        assertEquals("username", user.getUsername());
        assertEquals("hash", user.getPasswordHash());
        assertEquals("user@example.com", user.getEmail());
        assertFalse(user.isMfaEnabled());
        assertEquals("mfaSecret", user.getMfaSecret());
        assertTrue(user.isFirstAccessRequired());
        assertTrue(user.isEnabled());
        assertNotNull(user.getRoles());
        assertEquals(1, user.getRoles().size());
        assertEquals("ROLE_ADMIN", user.getRoles().get(0).getName());
    }

    @Test
    void testEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        UserEntity user1 = new UserEntity(id, "user1", "hash1", "a@a.com", false, "", true, true, List.of());
        UserEntity user2 = new UserEntity(id, "user2", "hash2", "b@b.com", true, "secret", false, false, List.of());

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }
}
