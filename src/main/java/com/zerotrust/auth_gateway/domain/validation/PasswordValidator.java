package com.zerotrust.auth_gateway.domain.validation;

import com.zerotrust.auth_gateway.domain.exception.InvalidPasswordException;

import java.util.regex.Pattern;

public final class PasswordValidator {

    private PasswordValidator() {
    }

    private static final int MIN_LENGTH = 6;

    private static final Pattern UPPERCASE = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE = Pattern.compile("[a-z]");
    private static final Pattern DIGIT = Pattern.compile("\\d");
    private static final Pattern SPECIAL = Pattern.compile("[^a-zA-Z0-9]");

    public static void validate(String password, String confirmedPassword) {
        if (password == null || password.length() < MIN_LENGTH) {
            throw new InvalidPasswordException("Password must be at least " + MIN_LENGTH + " characters long.");
        }

        if (!password.equals(confirmedPassword)) {
            throw new InvalidPasswordException("Password and confirmation password do not match.");
        }

        if (!UPPERCASE.matcher(password).find()) {
            throw new InvalidPasswordException("Password must contain at least one uppercase letter");
        }

        if (!LOWERCASE.matcher(password).find()) {
            throw new InvalidPasswordException("Password must contain at least one lowercase letter");
        }

        if (!DIGIT.matcher(password).find()) {
            throw new InvalidPasswordException("Password must contain at least one number");
        }

        if (!SPECIAL.matcher(password).find()) {
            throw new InvalidPasswordException("Password must contain at least one special character");
        }
    }
}
