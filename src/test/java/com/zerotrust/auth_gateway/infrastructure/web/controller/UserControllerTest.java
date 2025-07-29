package com.zerotrust.auth_gateway.infrastructure.web.controller;

import com.zerotrust.auth_gateway.application.usecase.interfaces.activation.AccountActivationUseCase;
import com.zerotrust.auth_gateway.application.usecase.interfaces.registration.PublicRegistrationUseCase;
import com.zerotrust.auth_gateway.application.dto.request.password.PasswordResetRequest;
import com.zerotrust.auth_gateway.application.dto.request.registration.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    private PublicRegistrationUseCase publicRegistrationUseCase;
    private AccountActivationUseCase accountActivationUseCase;
    private UserController userController;

    @BeforeEach
    void setUp() {
        publicRegistrationUseCase = mock(PublicRegistrationUseCase.class);
        accountActivationUseCase = mock(AccountActivationUseCase.class);

        userController = new UserController(publicRegistrationUseCase, accountActivationUseCase);
    }

    @Test
    void shouldRegisterUserAndReturnCreatedStatus() {
        RegisterRequest request = new RegisterRequest("username", "Password1@", "user@example.com", List.of("ROLE_USER"), true, "Password1@", false);

        ResponseEntity<Void> response = userController.register(request);

        ArgumentCaptor<RegisterRequest> captor = ArgumentCaptor.forClass(RegisterRequest.class);
        verify(publicRegistrationUseCase, times(1)).register(captor.capture());

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

        verify(accountActivationUseCase, times(1)).activate(token, passwordResetRequest);
        assertEquals(200, response.getStatusCodeValue());
        assertNull(response.getBody());
    }
}
