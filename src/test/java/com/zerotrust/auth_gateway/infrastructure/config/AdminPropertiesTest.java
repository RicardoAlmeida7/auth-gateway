package com.zerotrust.auth_gateway.infrastructure.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdminPropertiesTest {

    @Test
    void shouldSetAndGetPropertiesCorrectly() {
        AdminProperties properties = new AdminProperties();
        properties.setUsername("admin");
        properties.setPassword("securepass");

        assertEquals("admin", properties.getUsername());
        assertEquals("securepass", properties.getPassword());
    }

    @Test
    void shouldHaveDefaultConstructor() {
        assertDoesNotThrow(() -> new AdminProperties());
    }
}
