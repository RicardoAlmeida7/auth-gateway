package com.zerotrust.auth_gateway.web.controller;

import com.zerotrust.auth_gateway.application.service.AuthService;
import com.zerotrust.auth_gateway.application.usecase.RegisterUserUseCase;
import com.zerotrust.auth_gateway.web.dto.JwtResponse;
import com.zerotrust.auth_gateway.web.dto.LoginRequest;
import com.zerotrust.auth_gateway.web.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    private RegisterUserUseCase registerUserUseCase;
    private AuthService authService;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        registerUserUseCase = mock(RegisterUserUseCase.class);
        authService = mock(AuthService.class);
        authController = new AuthController(registerUserUseCase, authService);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        // Given
        RegisterRequest request = new RegisterRequest("username", "password");

        // When
        ResponseEntity<Void> response = authController.register(request);

        // Then
        verify(registerUserUseCase, times(1)).register(any(RegisterRequest.class));
        assertEquals(200, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void shouldReturnJwtTokenOnLogin() {
        // Given
        LoginRequest request = new LoginRequest("username", "password");
        when(authService.login("username", "password")).thenReturn("mocked.jwt.token");

        // When
        ResponseEntity<JwtResponse> response = authController.login(request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals("mocked.jwt.token", response.getBody().getToken());
        assertEquals(200, response.getStatusCodeValue());
    }
}
