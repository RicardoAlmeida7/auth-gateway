package com.zerotrust.auth_gateway.domain.exception;

public class FirstAccessPasswordRequiredException extends RuntimeException {
    public FirstAccessPasswordRequiredException(String message) {
        super(message);
    }
}
