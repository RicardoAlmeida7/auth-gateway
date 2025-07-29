package com.zerotrust.auth_gateway.application.dto.response;

import com.zerotrust.auth_gateway.application.dto.response.auth.JwtResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JwtResponseTest {

    @Test
    void shouldCreateJwtResponseWithTokens() {
        // Given
        String accessToken = "access.mock.token";
        String refreshToken = "refresh.mock.token";

        // When
        JwtResponse response = new JwtResponse(accessToken, refreshToken);

        // Then
        assertEquals(accessToken, response.accessToken());
        assertEquals(refreshToken, response.refreshToken());
    }

    @Test
    void shouldImplementEqualsAndHashCodeProperly() {
        JwtResponse r1 = new JwtResponse("token123", "refresh123");
        JwtResponse r2 = new JwtResponse("token123", "refresh123");
        JwtResponse r3 = new JwtResponse("tokenXYZ", "refreshXYZ");

        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void shouldReturnMeaningfulToString() {
        JwtResponse response = new JwtResponse("abc.def.ghi", "xyz.uvw.123");
        String str = response.toString();
        assertTrue(str.contains("abc.def.ghi"));
        assertTrue(str.contains("xyz.uvw.123"));
    }
}
