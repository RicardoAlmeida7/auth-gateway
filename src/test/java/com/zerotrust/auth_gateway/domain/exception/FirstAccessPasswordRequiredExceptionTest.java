package com.zerotrust.auth_gateway.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FirstAccessPasswordRequiredExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        String message = "You must reset your password before logging in for the first time.";

        FirstAccessPasswordRequiredException exception = new FirstAccessPasswordRequiredException(message);

        assertEquals(message, exception.getMessage());
    }
}
