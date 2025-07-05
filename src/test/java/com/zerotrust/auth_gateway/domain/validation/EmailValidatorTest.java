package com.zerotrust.auth_gateway.domain.validation;

import com.zerotrust.auth_gateway.domain.exception.InvalidEmailException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EmailValidatorTest {

    @Test
    void shouldAcceptValidEmail() {
        assertDoesNotThrow(() -> EmailValidator.validate("user@example.com"));
        assertDoesNotThrow(() -> EmailValidator.validate("user23@mail.co"));
        assertDoesNotThrow(() -> EmailValidator.validate("a+b_c-d@mail.domain.com"));
    }

    @Test
    void shouldRejectNullEmail() {
        InvalidEmailException exception = assertThrows(InvalidEmailException.class,
                () -> EmailValidator.validate(null));
        assertEquals("Email must not be null or blank.", exception.getMessage());
    }

    @Test
    void shouldRejectBlankEmail() {
        InvalidEmailException exception = assertThrows(InvalidEmailException.class,
                () -> EmailValidator.validate("   "));
        assertEquals("Email must not be null or blank.", exception.getMessage());
    }

    @Test
    void shouldRejectInvalidEmailFormats() {
        assertThrows(InvalidEmailException.class, () -> EmailValidator.validate("plainaddress"));
        assertThrows(InvalidEmailException.class, () -> EmailValidator.validate("@no-user.com"));
        assertThrows(InvalidEmailException.class, () -> EmailValidator.validate("user@.com"));
        assertThrows(InvalidEmailException.class, () -> EmailValidator.validate("user@domain"));
        assertThrows(InvalidEmailException.class, () -> EmailValidator.validate("user@@domain.com"));
        assertThrows(InvalidEmailException.class, () -> EmailValidator.validate("user@domain..com"));
    }
}
