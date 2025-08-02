package com.zerotrust.auth_gateway.domain.exception;

public class UserBlockedException extends RuntimeException {
    public UserBlockedException(String message) {
        super(message);
    }
}
