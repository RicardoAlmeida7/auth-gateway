package com.zerotrust.auth_gateway.infrastructure.web.dto;

import com.zerotrust.auth_gateway.infrastructure.web.dto.LoginRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoginRequestTest {

    @Test
    void shouldCreateLoginRequestWithFields() {
        // Given
        String username = "user1";
        String password = "pass123";
        String otp = "123456";

        // When
        LoginRequest request = new LoginRequest(username, password, otp);

        // Then
        assertEquals(username, request.getUsername());
        assertEquals(password, request.getPassword());
        assertEquals(otp, request.getOtp());
    }

    @Test
    void shouldAllowFieldMutationViaSetters() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");
        request.setOtp("654321");

        assertEquals("admin", request.getUsername());
        assertEquals("admin123", request.getPassword());
        assertEquals("654321", request.getOtp());
    }

    @Test
    void shouldImplementEqualsAndHashCodeCorrectly() {
        LoginRequest r1 = new LoginRequest("u", "p");
        LoginRequest r2 = new LoginRequest("u", "p");
        LoginRequest r3 = new LoginRequest("x", "y");
        LoginRequest r4 = new LoginRequest("u", "p", "otpValue");

        assertEquals(r1, r2);
        assertEquals(r1, r4);
        assertNotEquals(r1, r3);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertEquals(r1.hashCode(), r4.hashCode());
    }

    @Test
    void shouldReturnMeaningfulToString() {
        LoginRequest request = new LoginRequest("someuser", "secret");
        String str = request.toString();
        assertTrue(str.contains("someuser"));
        assertTrue(str.contains("secret"));
    }
}
