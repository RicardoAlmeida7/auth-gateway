package com.zerotrust.auth_gateway.web.exception;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.zerotrust.auth_gateway.domain.exception.InvalidEmailException;
import com.zerotrust.auth_gateway.domain.exception.InvalidPasswordException;
import com.zerotrust.auth_gateway.domain.exception.InvalidUsernameException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void shouldHandleInvalidPasswordException() {
        String message = "Password must contain at least one uppercase letter";
        InvalidPasswordException ex = new InvalidPasswordException(message);

        ResponseEntity<Object> response = handler.handleInvalidPassword(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals(400, body.get("status"));
        assertEquals("Bad Request", body.get("error"));
        assertEquals(message, body.get("message"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void shouldHandleInvalidEmailException() {
        String message = "Email format is invalid.";
        InvalidEmailException ex = new InvalidEmailException(message);

        ResponseEntity<Object> response = handler.handleEmailException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals(400, body.get("status"));
        assertEquals("Bad Request", body.get("error"));
        assertEquals(message, body.get("message"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void shouldHandleInvalidUsernameException() {
        String message = "Username must not be null or blank.";
        InvalidUsernameException ex = new InvalidUsernameException(message);

        ResponseEntity<Object> response = handler.handleUsernameException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals(400, body.get("status"));
        assertEquals("Bad Request", body.get("error"));
        assertEquals(message, body.get("message"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void shouldHandleGenericException() {
        Exception ex = new Exception("Something went wrong");

        ResponseEntity<Object> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals(500, body.get("status"));
        assertEquals("Internal Server Error", body.get("error"));
        assertEquals("An unexpected error occurred.", body.get("message"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void shouldHandleTokenExpiredException() {
        String message = "The Token has expired on 2025-07-03T01:24:50Z";
        TokenExpiredException ex = new TokenExpiredException(message, Instant.now());

        ResponseEntity<Object> response = handler.handleTokenExpiredException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals(401, body.get("status"));
        assertEquals("Unauthorized", body.get("error"));
        assertEquals("Token has expired.", body.get("message"));
        assertNotNull(body.get("timestamp"));
    }

}
