package com.zerotrust.auth_gateway.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InvalidEmailExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        String message = "Invalid email format.";

        InvalidEmailException exception = new InvalidEmailException(message);

        assertEquals(message, exception.getMessage());
    }
}
