package com.zerotrust.auth_gateway.infrastructure.web.controller;

import com.zerotrust.auth_gateway.application.dto.request.AuthenticationRequest;
import com.zerotrust.auth_gateway.application.dto.response.JwtResponse;
import com.zerotrust.auth_gateway.application.usecase.interfaces.UserLoginUseCase;
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
    void shouldReturnJwtResponseOnLogin() {
        // Given
        AuthenticationRequest request = new AuthenticationRequest("username", "password", "123456");
        JwtResponse jwtResponse = new JwtResponse("mocked.access.token", "mocked.refresh.token");

        when(userLoginUseCase.login(request)).thenReturn(jwtResponse);

        // When
        ResponseEntity<JwtResponse> response = authController.login(request);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        JwtResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("mocked.access.token", body.accessToken());
        assertEquals("mocked.refresh.token", body.refreshToken());

        verify(userLoginUseCase, times(1)).login(request);
    }
}
