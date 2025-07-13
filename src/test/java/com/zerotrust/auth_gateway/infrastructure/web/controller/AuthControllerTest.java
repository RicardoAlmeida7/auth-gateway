package com.zerotrust.auth_gateway.infrastructure.web.controller;

import com.zerotrust.auth_gateway.application.dto.request.AuthenticationRequest;
import com.zerotrust.auth_gateway.application.usecase.interfaces.UserLoginUseCase;
import com.zerotrust.auth_gateway.application.dto.response.JwtResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    private UserLoginUseCase userLoginUseCase;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        userLoginUseCase = mock(UserLoginUseCase.class);
        authController = new AuthController(userLoginUseCase);
    }

    @Test
    void shouldReturnJwtTokenOnLogin() {
        // Given
        AuthenticationRequest request = new AuthenticationRequest("username", "password", null, null);
        when(userLoginUseCase.login(request)).thenReturn("mocked.jwt.token");

        // When
        ResponseEntity<JwtResponse> response = authController.login(request);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("mocked.jwt.token", response.getBody().getToken());

        verify(userLoginUseCase, times(1)).login(request);
    }
}
