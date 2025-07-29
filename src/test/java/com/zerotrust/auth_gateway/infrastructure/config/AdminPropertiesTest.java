package com.zerotrust.auth_gateway.infrastructure.config;

import com.zerotrust.auth_gateway.infrastructure.config.core.AdminProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdminPropertiesTest {

    @Test
    void shouldSetAndGetPropertiesCorrectly() {
        AdminProperties properties = new AdminProperties();
        properties.setUsername("admin");
        properties.setPassword("securepass");
        properties.setEmail("admin.@example.com");

        assertEquals("admin", properties.getUsername());
        assertEquals("securepass", properties.getPassword());
        assertEquals("admin.@example.com", properties.getEmail());
    }

    @Test
    void shouldHaveDefaultConstructor() {
        assertDoesNotThrow(() -> new AdminProperties());
    }
}
