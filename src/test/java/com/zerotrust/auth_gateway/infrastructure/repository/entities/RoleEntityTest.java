package com.zerotrust.auth_gateway.infrastructure.repository.entities;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class RoleEntityTest {

    @Test
    void shouldCreateRoleWithAllFields() {
        UUID id = UUID.randomUUID();
        RoleEntity role = new RoleEntity(id, "ROLE_ADMIN");

        assertEquals(id, role.getId());
        assertEquals("ROLE_ADMIN", role.getName());
    }

    @Test
    void shouldSetAndGetFieldsCorrectly() {
        RoleEntity role = new RoleEntity();
        UUID id = UUID.randomUUID();
        role.setId(id);
        role.setName("ROLE_USER");

        assertEquals(id, role.getId());
        assertEquals("ROLE_USER", role.getName());
    }

    @Test
    void testEqualityBasedOnIdAndName() {
        UUID id = UUID.randomUUID();
        RoleEntity role1 = new RoleEntity(id, "ROLE_TEST");
        RoleEntity role2 = new RoleEntity(id, "ROLE_TEST");

        assertEquals(role1.getId(), role2.getId());
        assertEquals(role1.getName(), role2.getName());
        assertNotSame(role1, role2); // Should be different objects
    }

    @Test
    void shouldHandleNullId() {
        RoleEntity role = new RoleEntity(null, "ROLE_NULL");
        assertNull(role.getId());
        assertEquals("ROLE_NULL", role.getName());
    }

    @Test
    void testToString() {
        RoleEntity role = new RoleEntity(UUID.randomUUID(), "ROLE_MANAGER");
        assertTrue(role.toString().contains("ROLE_MANAGER"));
    }
}
