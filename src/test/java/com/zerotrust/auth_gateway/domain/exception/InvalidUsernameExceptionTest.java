package com.zerotrust.auth_gateway.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InvalidUsernameExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        String message = "Invalid username.";

        InvalidUsernameException exception = new InvalidUsernameException(message);

        assertEquals(message, exception.getMessage());
    }
}
