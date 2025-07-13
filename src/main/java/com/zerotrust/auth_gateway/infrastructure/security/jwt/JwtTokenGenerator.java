package com.zerotrust.auth_gateway.infrastructure.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.zerotrust.auth_gateway.domain.exception.AuthenticationFailedException;

import java.time.Instant;
import java.util.Date;
import java.util.List;

public class JwtTokenGenerator {
    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final int EXPIRATION_ONE_HOUR = 3600;

    public JwtTokenGenerator(Algorithm algorithm, JWTVerifier verifier) {
        this.algorithm = algorithm;
        this.verifier = JWT.require(algorithm).build();
    }

    public String generateToken(String username, List<String> roles) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("Roles cannot be null or empty");
        }

        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(EXPIRATION_ONE_HOUR);

        return JWT.create()
                .withSubject(username)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiry))
                .withClaim("roles", roles)
                .sign(algorithm);
    }

    public String generateRefreshToken(String username) {
        Instant now = Instant.now();
        int refreshTokenExpiration = 604800;
        Instant expiry = now.plusSeconds(refreshTokenExpiration);

        return JWT.create()
                .withSubject(username)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiry))
                .withClaim("type", "refresh")
                .sign(algorithm);
    }

    public DecodedJWT verifyRefreshToken(String token) {
        DecodedJWT decodedJWT = verifyToken(token);
        String type = decodedJWT.getClaim("type").asString();
        if (!"refresh".equals(type)) {
            throw new AuthenticationFailedException("Invalid token type");
        }
        return decodedJWT;
    }

    public String generateActivationToken(String username, String email) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(EXPIRATION_ONE_HOUR);

        return JWT.create()
                .withSubject(username)
                .withClaim("email", email)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiry))
                .withClaim("type", "activation")
                .sign(algorithm);
    }

    public DecodedJWT verifyToken(String token) {
        try {
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new AuthenticationFailedException("Invalid or expired JWT token");
        }
    }
}
