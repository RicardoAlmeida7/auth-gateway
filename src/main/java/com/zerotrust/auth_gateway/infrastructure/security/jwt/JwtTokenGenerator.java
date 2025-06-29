package com.zerotrust.auth_gateway.infrastructure.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.time.Instant;
import java.util.Date;
import java.util.List;

public class JwtTokenGenerator {
    private final Algorithm algorithm;

    public JwtTokenGenerator(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public String generateToken(String username, List<String> roles) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("Roles cannot be null or empty");
        }

        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(3600);

        return JWT.create()
                .withSubject(username)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiry))
                .withClaim("roles", roles)
                .sign(algorithm);
    }
}
