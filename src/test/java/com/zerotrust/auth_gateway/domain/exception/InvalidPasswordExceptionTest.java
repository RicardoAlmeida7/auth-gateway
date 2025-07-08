package com.zerotrust.auth_gateway.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InvalidPasswordExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        String message = "Invalid password format.";

        InvalidPasswordException exception = new InvalidPasswordException(message);

        assertEquals(message, exception.getMessage());
    }
}
