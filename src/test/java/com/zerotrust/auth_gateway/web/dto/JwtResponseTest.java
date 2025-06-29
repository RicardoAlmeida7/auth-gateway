package com.zerotrust.auth_gateway.web.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JwtResponseTest {

    @Test
    void shouldCreateJwtResponseWithToken() {
        // Given
        String token = "ey.mock.jwt.token";

        // When
        JwtResponse response = new JwtResponse(token);

        // Then
        assertEquals(token, response.getToken());
    }

    @Test
    void shouldSetTokenSuccessfully() {
        // Given
        JwtResponse response = new JwtResponse("initial.token");

        // When
        response.setToken("updated.token");

        // Then
        assertEquals("updated.token", response.getToken());
    }

    @Test
    void shouldImplementEqualsAndHashCodeProperly() {
        JwtResponse r1 = new JwtResponse("token123");
        JwtResponse r2 = new JwtResponse("token123");
        JwtResponse r3 = new JwtResponse("other");

        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void shouldReturnMeaningfulToString() {
        JwtResponse response = new JwtResponse("abc.def.ghi");
        assertTrue(response.toString().contains("abc.def.ghi"));
    }
}
