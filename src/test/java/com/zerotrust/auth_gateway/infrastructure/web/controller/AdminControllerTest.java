package com.zerotrust.auth_gateway.infrastructure.web.controller;

import com.zerotrust.auth_gateway.application.dto.request.registration.RegisterRequest;
import com.zerotrust.auth_gateway.application.dto.response.user.ManagedUserResponse;
import com.zerotrust.auth_gateway.application.usecase.interfaces.admin.AdminUserManagementUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdminControllerTest {

    private AdminUserManagementUseCase adminUserManagementUseCase;
    private AdminController adminController;

    @BeforeEach
    void setup() {
        adminUserManagementUseCase = mock(AdminUserManagementUseCase.class);
        adminController = new AdminController(adminUserManagementUseCase);
    }

    @Test
    void shouldCreateUserWithFirstAccessRequiredTrue() {
        RegisterRequest request = new RegisterRequest(
                "admin",
                "Password1@",
                "admin@example.com",
                List.of("ROLE_ADMIN"),
                false,
                "Password1@",
                false
        );

        when(adminUserManagementUseCase.createUser(request)).thenReturn(new ManagedUserResponse(
                UUID.randomUUID(),
                request.getUsername(),
                request.getEmail(),
                false,
                false,
                false,
                request.getRoles()
        ));

        ResponseEntity<ManagedUserResponse> response = adminController.createUser(request);

        verify(adminUserManagementUseCase, times(1)).createUser(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
