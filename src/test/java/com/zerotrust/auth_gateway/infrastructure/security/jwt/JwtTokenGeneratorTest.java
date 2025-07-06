package com.zerotrust.auth_gateway.infrastructure.security.jwt;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JwtTokenGeneratorTest {

    private JwtTokenGenerator generator;
    private JWTVerifier verifier;

    @BeforeEach
    void setUp() {
        Algorithm algorithm = Algorithm.HMAC256("test-secret");
        verifier = mock(JWTVerifier.class);
        generator = new JwtTokenGenerator(algorithm, verifier);
    }

    @Test
    void shouldGenerateTokenWithValidInput() {
        String username = "user@example.com";
        List<String> roles = List.of("ROLE_USER");

        String token = generator.generateToken(username, roles);

        assertNotNull(token);
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

    @Test
    void shouldVerifyTokenSuccessfully() {
        String token = generator.generateToken("user", List.of("ROLE_USER"));

        assertDoesNotThrow(() -> {
            DecodedJWT decodedJWT = generator.verifyToken(token);
            assertEquals("user", decodedJWT.getSubject());
        });
    }

    @Test
    void shouldThrowRuntimeExceptionWhenVerifyFails() {
        String token = "invalid.token";

        when(verifier.verify(token)).thenThrow(new com.auth0.jwt.exceptions.JWTVerificationException("fail"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> generator.verifyToken(token));
        assertEquals("Invalid or expired JWT token", ex.getMessage());
    }

    @Test
    void shouldGenerateActivationTokenWithExpectedClaims() {
        String username = "testuser";
        String email = "testuser@example.com";

        String token = generator.generateActivationToken(username, email);

        assertNotNull(token);
        assertTrue(token.startsWith("ey"), "Token should start with JWT format");

        DecodedJWT decodedJWT = generator.verifyToken(token);

        assertEquals(username, decodedJWT.getSubject());
        assertEquals(email, decodedJWT.getClaim("email").asString());
        assertEquals("activation", decodedJWT.getClaim("type").asString());

        assertNotNull(decodedJWT.getIssuedAt());
        assertNotNull(decodedJWT.getExpiresAt());

        assertTrue(decodedJWT.getExpiresAt().after(decodedJWT.getIssuedAt()), "Expiry must be after issuedAt");
    }
}
