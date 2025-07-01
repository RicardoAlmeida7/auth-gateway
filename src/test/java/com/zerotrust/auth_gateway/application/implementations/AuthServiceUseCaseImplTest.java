package com.zerotrust.auth_gateway.application.implementations;

import com.zerotrust.auth_gateway.application.usecase.implementations.AuthServiceUseCaseImpl;
import com.zerotrust.auth_gateway.application.usecase.interfaces.AuthServiceUseCase;
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

public class AuthServiceUseCaseImplTest {
    private AuthenticationManager authenticationManager;
    private JwtTokenGenerator jwtTokenGenerator;
    private AuthServiceUseCase authServiceUseCaseImpl;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        jwtTokenGenerator = mock(JwtTokenGenerator.class);
        authServiceUseCaseImpl = new AuthServiceUseCaseImpl(authenticationManager, jwtTokenGenerator);
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

        String token = authServiceUseCaseImpl.login(username, password);

        assertEquals(expectedToken, token);
    }

    @Test
    void shouldThrowExceptionIfUsernameIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                authServiceUseCaseImpl.login(null, "123456")
        );
        assertEquals("Username cannot be null or blank.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfUsernameIsBlank() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                authServiceUseCaseImpl.login("   ", "123456")
        );
        assertEquals("Username cannot be null or blank.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfPasswordIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                authServiceUseCaseImpl.login("username", null)
        );
        assertEquals("Password cannot be null or blank.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfPasswordIsBlank() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                authServiceUseCaseImpl.login("username", "   ")
        );
        assertEquals("Password cannot be null or blank.", exception.getMessage());
    }
}
