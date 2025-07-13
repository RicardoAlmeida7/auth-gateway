package com.zerotrust.auth_gateway.infrastructure.web.controller;

import com.zerotrust.auth_gateway.application.usecase.interfaces.ActivateAccountUseCase;
import com.zerotrust.auth_gateway.application.usecase.interfaces.UserRegistrationUse;
import com.zerotrust.auth_gateway.application.dto.request.PasswordResetRequest;
import com.zerotrust.auth_gateway.application.dto.request.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    private UserRegistrationUse userRegistrationUse;
    private ActivateAccountUseCase activateAccountUseCase;
    private UserController userController;

    @BeforeEach
    void setUp() {
        userRegistrationUse = mock(UserRegistrationUse.class);
        activateAccountUseCase = mock(ActivateAccountUseCase.class);
        userController = new UserController(userRegistrationUse, activateAccountUseCase);
    }

    @Test
    void shouldRegisterUserAndReturnCreatedStatus() {
        RegisterRequest request = new RegisterRequest("username", "Password1@", "user@example.com", List.of("ROLE_USER"), true, "Password1@", false);

        ResponseEntity<Void> response = userController.register(request);

        ArgumentCaptor<RegisterRequest> captor = ArgumentCaptor.forClass(RegisterRequest.class);
        verify(userRegistrationUse, times(1)).register(captor.capture());

        RegisterRequest capturedRequest = captor.getValue();

        assertEquals(request.getUsername(), capturedRequest.getUsername());
        assertEquals(request.getPassword(), capturedRequest.getPassword());
        assertEquals(request.getEmail(), capturedRequest.getEmail());
        assertFalse(capturedRequest.isFirstAccessRequired());
        assertEquals(request.isMfaEnabled(), capturedRequest.isMfaEnabled());
        assertEquals(request.getConfirmPassword(), capturedRequest.getConfirmPassword());

        assertEquals(201, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void shouldActivateAccountAndReturnOkStatus() {
        String token = "some-valid-token";
        PasswordResetRequest passwordResetRequest = null; // teste com null

        ResponseEntity<Void> response = userController.activateAccount(token, passwordResetRequest);

        verify(activateAccountUseCase, times(1)).activate(token, passwordResetRequest);
        assertEquals(200, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void shouldCreateUserWithFirstAccessRequiredTrue() {
        RegisterRequest request = new RegisterRequest("admin", "Password1@", "admin@example.com", List.of("ROLE_ADMIN"), false, "Password1@", false);

        ResponseEntity<Void> response = userController.createUser(request);

        ArgumentCaptor<RegisterRequest> captor = ArgumentCaptor.forClass(RegisterRequest.class);
        verify(userRegistrationUse, times(1)).register(captor.capture());

        RegisterRequest capturedRequest = captor.getValue();
        assertTrue(capturedRequest.isFirstAccessRequired());

        assertEquals(201, response.getStatusCodeValue());
        assertNull(response.getBody());
    }
}
