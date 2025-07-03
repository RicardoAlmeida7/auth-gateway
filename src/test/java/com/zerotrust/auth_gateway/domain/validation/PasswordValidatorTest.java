package com.zerotrust.auth_gateway.domain.validation;

import com.zerotrust.auth_gateway.domain.exception.InvalidPasswordException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordValidatorTest {

    @Test
    void validPasswordDoesNotThrow() {
        String validPassword = "Abcdef1@";

        assertDoesNotThrow(() -> PasswordValidator.validate(validPassword));
    }

    @Test
    void nullPasswordThrows() {
        InvalidPasswordException ex = assertThrows(InvalidPasswordException.class, () -> PasswordValidator.validate(null));
        assertTrue(ex.getMessage().contains("at least"));
    }

    @Test
    void shortPasswordThrows() {
        InvalidPasswordException ex = assertThrows(InvalidPasswordException.class, () -> PasswordValidator.validate("Ab1@"));
        assertTrue(ex.getMessage().contains("at least"));
    }

    @Test
    void missingUppercaseThrows() {
        InvalidPasswordException ex = assertThrows(InvalidPasswordException.class, () -> PasswordValidator.validate("abcdef1@"));
        assertEquals("Password must contain at least one uppercase letter", ex.getMessage());
    }

    @Test
    void missingLowercaseThrows() {
        InvalidPasswordException ex = assertThrows(InvalidPasswordException.class, () -> PasswordValidator.validate("ABCDEF1@"));
        assertEquals("Password must contain at least one lowercase letter", ex.getMessage());
    }

    @Test
    void missingDigitThrows() {
        InvalidPasswordException ex = assertThrows(InvalidPasswordException.class, () -> PasswordValidator.validate("Abcdefg@"));
        assertEquals("Password must contain at least one number", ex.getMessage());
    }

    @Test
    void missingSpecialCharThrows() {
        InvalidPasswordException ex = assertThrows(InvalidPasswordException.class, () -> PasswordValidator.validate("Abcdefg1"));
        assertEquals("Password must contain at least one special character", ex.getMessage());
    }
}
