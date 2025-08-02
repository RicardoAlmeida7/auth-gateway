package com.zerotrust.auth_gateway.domain.exception;

public class UserAlreadyActivatedException extends RuntimeException {
    public UserAlreadyActivatedException(String message) {
        super(message);
    }
}
