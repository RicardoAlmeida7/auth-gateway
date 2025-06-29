package com.zerotrust.auth_gateway.infrastructure.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.zerotrust.auth_gateway.infrastructure.security.jwt.JwtTokenGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class JwtConfig {

    @Bean
    public Algorithm algorithm() {
        return Algorithm.RSA256(loadPublicKey(), loadPrivateKey());
    }

    @Bean
    public JWTVerifier jwtVerifier(Algorithm algorithm) {
        return JWT.require(algorithm).build();
    }

    @Bean
    public JwtTokenGenerator jwtTokenGenerator(Algorithm algorithm, JWTVerifier verifier) {
        return new JwtTokenGenerator(algorithm, verifier);
    }

    RSAPrivateKey loadPrivateKey() {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("keys/private.pem");
            if (inputStream == null) throw new RuntimeException("Private key file not found");
            String key = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] decoded = Base64.getDecoder().decode(key);
            return (RSAPrivateKey) KeyFactory.getInstance("RSA")
                    .generatePrivate(new PKCS8EncodedKeySpec(decoded));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load private key", e);
        }
    }

    RSAPublicKey loadPublicKey() {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("keys/public.pem");
            if (inputStream == null) throw new RuntimeException("Public key file not found");
            String key = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] decoded = Base64.getDecoder().decode(key);
            return (RSAPublicKey) KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(decoded));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load public key", e);
        }
    }
}
