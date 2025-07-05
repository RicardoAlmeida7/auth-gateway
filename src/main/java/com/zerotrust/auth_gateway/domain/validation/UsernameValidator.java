package com.zerotrust.auth_gateway.domain.validation;

import com.zerotrust.auth_gateway.domain.exception.InvalidUsernameException;

import java.util.regex.Pattern;

public class UsernameValidator {

    private UsernameValidator() {}

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9](?!.*[_.]{2})[a-zA-Z0-9._]{1,28}[a-zA-Z0-9]$");

    public static void validate(String username) {
        if (username == null || username.isBlank()) {
            throw new InvalidUsernameException("Username must not be null or blank.");
        }

        if (username.length() < 3 || username.length() > 30) {
            throw new InvalidUsernameException("Username must be between 3 and 30 characters.");
        }

        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new InvalidUsernameException("Username contains invalid characters or format.");
        }
    }
}
