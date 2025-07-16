package com.zerotrust.auth_gateway.application.dto.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordResetRequestTest {

    @Test
    void shouldCreateInstanceWithDefaultConstructor() {
        PasswordResetRequest request = new PasswordResetRequest();
        assertNull(request.getNewPassword());
        assertNull(request.getConfirmPassword());
    }

    @Test
    void shouldCreateInstanceWithAllArgsConstructor() {
        PasswordResetRequest request = new PasswordResetRequest("newPass123", "newPass123");
        assertEquals("newPass123", request.getNewPassword());
        assertEquals("newPass123", request.getConfirmPassword());
    }

    @Test
    void shouldSetAndGetNewPassword() {
        PasswordResetRequest request = new PasswordResetRequest();
        request.setNewPassword("abc123");
        assertEquals("abc123", request.getNewPassword());
    }

    @Test
    void shouldSetAndGetConfirmPassword() {
        PasswordResetRequest request = new PasswordResetRequest();
        request.setConfirmPassword("abc123");
        assertEquals("abc123", request.getConfirmPassword());
    }

    @Test
    void shouldAllowNewPasswordAndConfirmPasswordToDiffer() {
        PasswordResetRequest request = new PasswordResetRequest("pass1", "pass2");
        assertEquals("pass1", request.getNewPassword());
        assertEquals("pass2", request.getConfirmPassword());
        assertNotEquals(request.getNewPassword(), request.getConfirmPassword());
    }
}
