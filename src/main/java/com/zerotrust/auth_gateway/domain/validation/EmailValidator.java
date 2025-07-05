package com.zerotrust.auth_gateway.domain.validation;

import com.zerotrust.auth_gateway.domain.exception.InvalidEmailException;

import java.util.regex.Pattern;

public class EmailValidator {

    private EmailValidator() {}

    // RFC 5322 https://emailregex.com/
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "(?:[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"" +
                    "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|" +
                    "\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@" +
                    "(?:(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+" +
                    "[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?|\\[" +
                    "(?:(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})\\.){3}" +
                    "(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})])"
    );

    public static void validate(String email) {
        if (email == null || email.isBlank()) {
            throw new InvalidEmailException("Email must not be null or blank.");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidEmailException("Email format is invalid.");
        }
    }
}
