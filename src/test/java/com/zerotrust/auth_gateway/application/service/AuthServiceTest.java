package com.zerotrust.auth_gateway.application.service;

import com.zerotrust.auth_gateway.infrastructure.security.jwt.JwtTokenGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthServiceTest {
    private AuthenticationManager authenticationManager;
    private JwtTokenGenerator jwtTokenGenerator;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        jwtTokenGenerator = mock(JwtTokenGenerator.class);
        authService = new AuthService(authenticationManager, jwtTokenGenerator);
    }

    @Test
    void shouldLoginAndReturnToken() {
        String username = "username";
        String password = "123456";
        String expectedToken = "token123";

        Authentication mockAuth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);

        when(mockAuth.getAuthorities())
                .thenReturn((Collection) List.of(new SimpleGrantedAuthority("ROLE_USER")));

        when(jwtTokenGenerator.generateToken(username, List.of("ROLE_USER")))
                .thenReturn(expectedToken);

        String token = authService.login(username, password);

        assertEquals(expectedToken, token);
    }

    @Test
    void shouldThrowExceptionIfUsernameIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                authService.login(null, "123456")
        );
        assertEquals("Username cannot be null or blank.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfUsernameIsBlank() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                authService.login("   ", "123456")
        );
        assertEquals("Username cannot be null or blank.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfPasswordIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                authService.login("username", null)
        );
        assertEquals("Password cannot be null or blank.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfPasswordIsBlank() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                authService.login("username", "   ")
        );
        assertEquals("Password cannot be null or blank.", exception.getMessage());
    }
}
