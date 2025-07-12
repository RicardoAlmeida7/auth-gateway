package com.zerotrust.auth_gateway.infrastructure.web.controller;

import com.zerotrust.auth_gateway.application.dto.AuthenticationRequest;
import com.zerotrust.auth_gateway.application.usecase.interfaces.AuthServiceUseCase;
import com.zerotrust.auth_gateway.application.dto.JwtResponse;
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
        AuthenticationRequest request = new AuthenticationRequest("username", "password", null, null);
        when(authServiceUseCase.login(request)).thenReturn("mocked.jwt.token");

        // When
        ResponseEntity<JwtResponse> response = authController.login(request);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("mocked.jwt.token", response.getBody().getToken());

        verify(authServiceUseCase, times(1)).login(request);
    }
}
