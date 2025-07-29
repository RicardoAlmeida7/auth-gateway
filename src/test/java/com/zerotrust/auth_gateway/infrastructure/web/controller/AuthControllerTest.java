package com.zerotrust.auth_gateway.infrastructure.web.controller;

import com.zerotrust.auth_gateway.application.dto.request.auth.AuthenticationRequest;
import com.zerotrust.auth_gateway.application.dto.response.auth.JwtResponse;
import com.zerotrust.auth_gateway.application.usecase.interfaces.auth.AuthenticationUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    private AuthenticationUseCase authenticationUseCase;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        authenticationUseCase = mock(AuthenticationUseCase.class);
        authController = new AuthController(authenticationUseCase);
    }

    @Test
    void shouldReturnJwtResponseOnLogin() {
        // Given
        AuthenticationRequest request = new AuthenticationRequest("username", "password", "123456");
        JwtResponse jwtResponse = new JwtResponse("mocked.access.token", "mocked.refresh.token");

        when(authenticationUseCase.login(request)).thenReturn(jwtResponse);

        // When
        ResponseEntity<JwtResponse> response = authController.login(request);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        JwtResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("mocked.access.token", body.accessToken());
        assertEquals("mocked.refresh.token", body.refreshToken());

        verify(authenticationUseCase, times(1)).login(request);
    }
}
