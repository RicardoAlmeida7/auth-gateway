package com.zerotrust.auth_gateway.web.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoginRequestTest {

    @Test
    void shouldCreateLoginRequestWithFields() {
        // Given
        String username = "user1";
        String password = "pass123";

        // When
        LoginRequest request = new LoginRequest(username, password);

        // Then
        assertEquals(username, request.getUsername());
        assertEquals(password, request.getPassword());
    }

    @Test
    void shouldAllowFieldMutationViaSetters() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");

        assertEquals("admin", request.getUsername());
        assertEquals("admin123", request.getPassword());
    }

    @Test
    void shouldImplementEqualsAndHashCodeCorrectly() {
        LoginRequest r1 = new LoginRequest("u", "p");
        LoginRequest r2 = new LoginRequest("u", "p");
        LoginRequest r3 = new LoginRequest("x", "y");

        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void shouldReturnMeaningfulToString() {
        LoginRequest request = new LoginRequest("someuser", "secret");
        String str = request.toString();
        assertTrue(str.contains("someuser"));
        assertTrue(str.contains("secret"));
    }
}
