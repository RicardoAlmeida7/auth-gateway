package com.zerotrust.auth_gateway.infrastructure.config;

import com.zerotrust.auth_gateway.infrastructure.security.jwt.JwtTokenGenerator;
import org.junit.jupiter.api.Test;

import java.security.interfaces.RSAPrivateKey;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JwtConfigTest {

    @Test
    void shouldReturnJwtTokenGeneratorInstance() {
        // Arrange
        JwtConfig config = spy(new JwtConfig());

        RSAPrivateKey mockPrivateKey = mock(RSAPrivateKey.class);
        doReturn(mockPrivateKey).when(config).loadPrivateKey();

        // Act
        JwtTokenGenerator generator = config.jwtTokenGenerator();

        // Assert
        assertNotNull(generator, "Expected a non-null JwtTokenGenerator");
    }

    // Test failure loading key throws exception
    @Test
    void shouldThrowRuntimeExceptionWhenKeyFailsToLoad() {
        JwtConfig config = new JwtConfig() {
            @Override
            protected RSAPrivateKey loadPrivateKey() {
                throw new RuntimeException("Simulated failure");
            }
        };

        RuntimeException ex = assertThrows(RuntimeException.class, config::jwtTokenGenerator);
        assertEquals("Simulated failure", ex.getMessage());
    }
}
