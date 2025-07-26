package com.zerotrust.auth_gateway.infrastructure.web.controller;

import com.zerotrust.auth_gateway.application.dto.request.RegisterRequest;
import com.zerotrust.auth_gateway.application.dto.response.ManagedUserResponse;
import com.zerotrust.auth_gateway.application.usecase.interfaces.UserManagementUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdminControllerTest {

    private UserManagementUseCase userManagementUseCase;
    private AdminController adminController;

    @BeforeEach
    void setup() {
        userManagementUseCase = mock(UserManagementUseCase.class);
        adminController = new AdminController(userManagementUseCase);
    }

    @Test
    void shouldCreateUserWithFirstAccessRequiredTrue() {
        RegisterRequest request = new RegisterRequest("admin", "Password1@", "admin@example.com", List.of("ROLE_ADMIN"), false, "Password1@", false);

        when(userManagementUseCase.createUser(request)).thenReturn(new ManagedUserResponse(UUID.randomUUID(), request.getUsername(), request.getEmail(), false, request.getRoles()));

        ResponseEntity<ManagedUserResponse> response = adminController.createUser(request);

        verify(userManagementUseCase, times(1)).createUser(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
