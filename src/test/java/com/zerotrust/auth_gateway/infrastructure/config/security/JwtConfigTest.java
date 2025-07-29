package com.zerotrust.auth_gateway.infrastructure.config.security;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.zerotrust.auth_gateway.infrastructure.security.jwt.JwtTokenGenerator;
import org.junit.jupiter.api.Test;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JwtConfigTest {

    @Test
    void shouldCreateAlgorithmWithKeys() {
        JwtConfig config = spy(new JwtConfig());

        RSAPrivateKey mockPrivateKey = mock(RSAPrivateKey.class);
        RSAPublicKey mockPublicKey = mock(RSAPublicKey.class);

        doReturn(mockPrivateKey).when(config).loadPrivateKey();
        doReturn(mockPublicKey).when(config).loadPublicKey();

        Algorithm algorithm = config.algorithm();

        assertNotNull(algorithm);
    }

    @Test
    void shouldCreateJwtVerifier() {
        JwtConfig config = new JwtConfig();
        Algorithm algorithm = mock(Algorithm.class);
        JWTVerifier verifier = config.jwtVerifier(algorithm);
        assertNotNull(verifier);
    }

    @Test
    void shouldCreateJwtTokenGenerator() {
        Algorithm algorithm = mock(Algorithm.class);

        JwtConfig config = new JwtConfig();
        JwtTokenGenerator generator = config.jwtTokenGenerator(algorithm);

        assertNotNull(generator);
    }

    @Test
    void shouldThrowExceptionWhenPrivateKeyNotFound() {
        JwtConfig config = new JwtConfig() {
            @Override
            public RSAPrivateKey loadPrivateKey() {
                throw new RuntimeException("Simulated private key load error");
            }

            @Override
            public RSAPublicKey loadPublicKey() {
                return mock(RSAPublicKey.class);
            }
        };

        RuntimeException ex = assertThrows(RuntimeException.class, config::algorithm);
        assertTrue(ex.getMessage().contains("Simulated"));
    }

    @Test
    void shouldThrowExceptionWhenPublicKeyNotFound() {
        JwtConfig config = new JwtConfig() {
            @Override
            public RSAPublicKey loadPublicKey() {
                throw new RuntimeException("Simulated public key load error");
            }

            @Override
            public RSAPrivateKey loadPrivateKey() {
                return mock(RSAPrivateKey.class);
            }
        };

        RuntimeException ex = assertThrows(RuntimeException.class, config::algorithm);
        assertTrue(ex.getMessage().contains("Simulated"));
    }

    @Test
    void loadPrivateKey_shouldLoadKeySuccessfully() {
        JwtConfig config = new JwtConfig();
        RSAPrivateKey privateKey = config.loadPrivateKey();
        assertNotNull(privateKey);
    }

    @Test
    void loadPublicKey_shouldLoadKeySuccessfully() {
        JwtConfig config = new JwtConfig();
        RSAPublicKey publicKey = config.loadPublicKey();
        assertNotNull(publicKey);
    }
}
