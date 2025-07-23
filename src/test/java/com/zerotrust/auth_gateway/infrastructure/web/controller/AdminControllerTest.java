package com.zerotrust.auth_gateway.infrastructure.web.controller;

import com.zerotrust.auth_gateway.application.dto.request.RegisterRequest;
import com.zerotrust.auth_gateway.application.usecase.interfaces.UserManagementUseCase;
import com.zerotrust.auth_gateway.application.usecase.interfaces.UserRegistrationUse;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdminControllerTest {

    private UserRegistrationUse userRegistrationUse;
    private UserManagementUseCase userManagementUseCase;
    private AdminController adminController;

    @BeforeEach
    void setup() {
        userRegistrationUse = mock(UserRegistrationUse.class);
        userManagementUseCase = mock(UserManagementUseCase.class);
        adminController = new AdminController(userRegistrationUse, userManagementUseCase);
    }

    @Test
    void shouldCreateUserWithFirstAccessRequiredTrue() {
        RegisterRequest request = new RegisterRequest("admin", "Password1@", "admin@example.com", List.of("ROLE_ADMIN"), false, "Password1@", false);

        ResponseEntity<Void> response = adminController.createUser(request);

        ArgumentCaptor<RegisterRequest> captor = ArgumentCaptor.forClass(RegisterRequest.class);
        verify(userRegistrationUse, times(1)).register(captor.capture());

        RegisterRequest capturedRequest = captor.getValue();
        assertTrue(capturedRequest.isFirstAccessRequired());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNull(response.getBody());
    }
}
