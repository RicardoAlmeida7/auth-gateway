package com.zerotrust.auth_gateway.domain.validation;

import com.zerotrust.auth_gateway.domain.exception.InvalidPasswordException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordValidatorTest {

    @Test
    void validPasswordDoesNotThrow() {
        String validPassword = "Abcdef1@";
        String confirmedPassword = "Abcdef1@";

        assertDoesNotThrow(() -> PasswordValidator.validate(validPassword, confirmedPassword));
    }

    @Test
    void nullPasswordThrows() {
        InvalidPasswordException ex = assertThrows(InvalidPasswordException.class,
                () -> PasswordValidator.validate(null, null));
        assertTrue(ex.getMessage().contains("at least"));
    }

    @Test
    void shortPasswordThrows() {
        InvalidPasswordException ex = assertThrows(InvalidPasswordException.class,
                () -> PasswordValidator.validate("Ab1@", "Ab1@"));
        assertTrue(ex.getMessage().contains("at least"));
    }

    @Test
    void missingUppercaseThrows() {
        InvalidPasswordException ex = assertThrows(InvalidPasswordException.class,
                () -> PasswordValidator.validate("abcdef1@", "abcdef1@"));
        assertEquals("Password must contain at least one uppercase letter", ex.getMessage());
    }

    @Test
    void missingLowercaseThrows() {
        InvalidPasswordException ex = assertThrows(InvalidPasswordException.class,
                () -> PasswordValidator.validate("ABCDEF1@", "ABCDEF1@"));
        assertEquals("Password must contain at least one lowercase letter", ex.getMessage());
    }

    @Test
    void missingDigitThrows() {
        InvalidPasswordException ex = assertThrows(InvalidPasswordException.class,
                () -> PasswordValidator.validate("Abcdefg@", "Abcdefg@"));
        assertEquals("Password must contain at least one number", ex.getMessage());
    }

    @Test
    void missingSpecialCharThrows() {
        InvalidPasswordException ex = assertThrows(InvalidPasswordException.class,
                () -> PasswordValidator.validate("Abcdefg1", "Abcdefg1"));
        assertEquals("Password must contain at least one special character", ex.getMessage());
    }

    @Test
    void passwordAndConfirmationMismatchThrows() {
        InvalidPasswordException ex = assertThrows(InvalidPasswordException.class,
                () -> PasswordValidator.validate("Abcdef1@", "Mismatch1@"));
        assertEquals("Password and confirmation password do not match.", ex.getMessage());
    }
}
