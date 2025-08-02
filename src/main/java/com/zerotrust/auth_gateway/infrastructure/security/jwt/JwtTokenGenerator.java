package com.zerotrust.auth_gateway.infrastructure.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.zerotrust.auth_gateway.domain.exception.AuthenticationFailedException;
import com.zerotrust.auth_gateway.domain.utils.Constants;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class JwtTokenGenerator {

    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public JwtTokenGenerator(Algorithm algorithm) {
        this.algorithm = algorithm;
        this.verifier = JWT.require(algorithm).build();
    }

    public String generateToken(UUID userId, String username, List<String> roles) {
        if (userId == null) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("Roles cannot be null or empty");
        }

        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(Constants.AUTHENTICATION_TOKEN_TTL_SECONDS);

        return JWT.create()
                .withSubject(userId.toString())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiry))
                .withClaim("username", username)
                .withClaim("roles", roles)
                .sign(algorithm);
    }

    public String generateRefreshToken(UUID userId, String username) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(Constants.REFRESH_TOKEN_TTL_SECONDS);

        return JWT.create()
                .withSubject(userId.toString())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiry))
                .withClaim("type", Constants.REFRESH_TOKEN_TYPE)
                .withClaim("username", username)
                .sign(algorithm);
    }

    public DecodedJWT verifyRefreshToken(String token) {
        DecodedJWT decodedJWT = verifyToken(token);
        String type = decodedJWT.getClaim("type").asString();
        if (!Constants.REFRESH_TOKEN_TYPE.equals(type)) {
            throw new AuthenticationFailedException("Invalid token type");
        }
        return decodedJWT;
    }

    public String generateActivationToken(UUID userId, String username, String email) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(Constants.ACTIVATION_TOKEN_TTL_SECONDS);

        return JWT.create()
                .withSubject(userId.toString())
                .withClaim("email", email)
                .withClaim("username", username)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiry))
                .withClaim("type", Constants.ACTIVATION_TOKEN_TYPE)
                .sign(algorithm);
    }

    public DecodedJWT verifyActivationToken(String token) {
        DecodedJWT decodedJWT = verifyToken(token);
        String type = decodedJWT.getClaim("type").asString();
        if (!Constants.ACTIVATION_TOKEN_TYPE.equals(type)) {
            throw new AuthenticationFailedException("Invalid token type");
        }
        return decodedJWT;
    }

    public String generateResetPasswordToken(UUID userId, String username, String email) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(Constants.RESET_PASSWORD_TOKEN_TTL_SECONDS);

        return JWT.create()
                .withSubject(userId.toString())
                .withClaim("email", email)
                .withClaim("username", username)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiry))
                .withClaim("type", Constants.RESET_PASSWORD_TOKEN_TYPE)
                .sign(algorithm);
    }

    public DecodedJWT verifyResetPasswordToken(String token) {
        DecodedJWT decodedJWT = verifyToken(token);
        String type = decodedJWT.getClaim("type").asString();
        if (!Constants.RESET_PASSWORD_TOKEN_TYPE.equals(type)) {
            throw new AuthenticationFailedException("Invalid token type");
        }
        return decodedJWT;
    }

    public DecodedJWT verifyToken(String token) {
        try {
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new AuthenticationFailedException("Invalid or expired JWT token");
        }
    }
}
