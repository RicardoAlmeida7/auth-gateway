package com.zerotrust.auth_gateway.infrastructure.security.jwt;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class JwtTokenGeneratorTest {

    private JwtTokenGenerator generator;

    @BeforeEach
    void setUp() {
        Algorithm algorithm = Algorithm.HMAC256("test-secret");
        generator = new JwtTokenGenerator(algorithm);
    }

    @Test
    void shouldGenerateTokenWithValidInput() {
        String username = "user@example.com";
        List<String> roles = List.of("ROLE_USER");
        UUID userId = UUID.randomUUID();

        String token = generator.generateToken(userId, username, roles);

        assertNotNull(token);
        assertTrue(token.startsWith("ey"), "Token should be a valid JWT string");
    }

    @Test
    void shouldThrowExceptionWhenUsernameIsNull() {
        List<String> roles = List.of("ROLE_USER");
        assertThrows(IllegalArgumentException.class, () -> generator.generateToken(null, "username", roles));
    }

    @Test
    void shouldThrowExceptionWhenRolesIsNull() {
        assertThrows(IllegalArgumentException.class, () -> generator.generateToken(UUID.randomUUID(), "user", null));
    }

    @Test
    void shouldThrowExceptionWhenRolesIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> generator.generateToken(UUID.randomUUID(), "username", List.of()));
    }

    @Test
    void shouldVerifyTokenSuccessfully() {
        UUID userId = UUID.randomUUID();
        String token = generator.generateToken(userId, "user", List.of("ROLE_USER"));

        assertDoesNotThrow(() -> {
            DecodedJWT decodedJWT = generator.verifyToken(token);
            assertEquals(userId, UUID.fromString(decodedJWT.getSubject()));
        });
    }

    @Test
    void shouldThrowRuntimeExceptionWhenVerifyFails() {
        String token = "invalid.token";

        RuntimeException ex = assertThrows(RuntimeException.class, () -> generator.verifyToken(token));
        assertEquals("Invalid or expired JWT token", ex.getMessage());
    }

    @Test
    void shouldGenerateActivationTokenWithExpectedClaims() {
        UUID userId = UUID.randomUUID();
        String username = "testuser";
        String email = "testuser@example.com";

        String token = generator.generateActivationToken(userId, username, email);

        assertNotNull(token);
        assertTrue(token.startsWith("ey"), "Token should start with JWT format");

        DecodedJWT decodedJWT = generator.verifyToken(token);

        assertEquals(userId.toString(), decodedJWT.getSubject());
        assertEquals(email, decodedJWT.getClaim("email").asString());
        assertEquals(username, decodedJWT.getClaim("username").asString());
        assertEquals("activation_token", decodedJWT.getClaim("type").asString());

        assertNotNull(decodedJWT.getIssuedAt());
        assertNotNull(decodedJWT.getExpiresAt());

        assertTrue(decodedJWT.getExpiresAt().after(decodedJWT.getIssuedAt()), "Expiry must be after issuedAt");
    }
}
