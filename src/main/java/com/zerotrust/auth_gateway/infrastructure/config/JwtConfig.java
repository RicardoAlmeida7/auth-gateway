package com.zerotrust.auth_gateway.infrastructure.config;

import com.auth0.jwt.algorithms.Algorithm;
import com.zerotrust.auth_gateway.infrastructure.security.jwt.JwtTokenGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Configuration
public class JwtConfig {

    @Bean
    public JwtTokenGenerator jwtTokenGenerator() {
        RSAPrivateKey privateKey = loadPrivateKey();
        Algorithm algorithm = Algorithm.RSA256(null, privateKey);
        return new JwtTokenGenerator(algorithm);
    }

    RSAPrivateKey loadPrivateKey() {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("keys/private.pem");
            String key = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] decoded = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);

            return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load private key", e);
        }
    }
}
