package com.zerotrust.auth_gateway.web.controller;

import com.zerotrust.auth_gateway.application.service.AuthService;
import com.zerotrust.auth_gateway.application.usecase.RegisterUserUseCase;
import com.zerotrust.auth_gateway.web.dto.JwtResponse;
import com.zerotrust.auth_gateway.web.dto.LoginRequest;
import com.zerotrust.auth_gateway.web.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        RegisterRequest request = new RegisterRequest("john.doe", "secret");

        // When
        ResponseEntity<Void> response = authController.register(request);

        // Then
        verify(registerUserUseCase).register(any());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void shouldReturnJwtTokenOnLogin() {
        // Given
        LoginRequest request = new LoginRequest("john.doe", "secret");
        when(authService.login("john.doe", "secret")).thenReturn("mocked.jwt.token");

        // When
        ResponseEntity<JwtResponse> response = authController.login(request);

        // Then
        assertNotNull(response.getBody());
        assertEquals("mocked.jwt.token", response.getBody().getToken());
        assertEquals(200, response.getStatusCodeValue());
    }
}
