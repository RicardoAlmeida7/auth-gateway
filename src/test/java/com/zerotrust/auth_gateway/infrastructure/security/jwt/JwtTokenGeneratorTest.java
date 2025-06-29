package com.zerotrust.auth_gateway.infrastructure.security.jwt;

import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JwtTokenGeneratorTest {

    private JwtTokenGenerator generator;

    @BeforeEach
    void setUp() {
        // Using a symmetric HMAC256 key for unit test
        Algorithm testAlgorithm = Algorithm.HMAC256("test-secret");
        generator = new JwtTokenGenerator(testAlgorithm);
    }

    @Test
    void shouldGenerateTokenWithValidInput() {
        String username = "user@example.com";
        List<String> roles = List.of("ROLE_USER");

        String token = generator.generateToken(username, roles);

        assertNotNull(token, "Token should not be null");
        assertTrue(token.startsWith("ey"), "Token should be a valid JWT string");
    }

    @Test
    void shouldThrowExceptionWhenUsernameIsNull() {
        List<String> roles = List.of("ROLE_USER");
        assertThrows(IllegalArgumentException.class, () -> generator.generateToken(null, roles));
    }

    @Test
    void shouldThrowExceptionWhenUsernameIsBlank() {
        List<String> roles = List.of("ROLE_USER");
        assertThrows(IllegalArgumentException.class, () -> generator.generateToken("   ", roles));
    }

    @Test
    void shouldThrowExceptionWhenRolesIsNull() {
        assertThrows(IllegalArgumentException.class, () -> generator.generateToken("user", null));
    }

    @Test
    void shouldThrowExceptionWhenRolesIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> generator.generateToken("user", List.of()));
    }
}
