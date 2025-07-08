package com.zerotrust.auth_gateway.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InvalidRoleExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        String message = "Invalid role specified.";

        InvalidRoleException exception = new InvalidRoleException(message);

        assertEquals(message, exception.getMessage());
    }
}
