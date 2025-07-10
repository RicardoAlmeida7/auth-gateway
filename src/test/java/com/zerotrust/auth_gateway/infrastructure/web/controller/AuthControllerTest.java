package com.zerotrust.auth_gateway.infrastructure.web.controller;

import com.zerotrust.auth_gateway.application.usecase.interfaces.AuthServiceUseCase;
import com.zerotrust.auth_gateway.application.dto.JwtResponse;
import com.zerotrust.auth_gateway.application.dto.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    private AuthServiceUseCase authServiceUseCase;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        authServiceUseCase = mock(AuthServiceUseCase.class);
        authController = new AuthController(authServiceUseCase);
    }

    @Test
    void shouldReturnJwtTokenOnLogin() {
        // Given
        LoginRequest request = new LoginRequest("username", "password", null);
        when(authServiceUseCase.login("username", "password", null)).thenReturn("mocked.jwt.token");

        // When
        ResponseEntity<JwtResponse> response = authController.login(request);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("mocked.jwt.token", response.getBody().getToken());

        verify(authServiceUseCase, times(1)).login("username", "password", null);
    }
}
