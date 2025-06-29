package com.zerotrust.auth_gateway.infrastructure.security.providers;

import com.zerotrust.auth_gateway.infrastructure.security.userdetails.UserDetailsServiceAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CustomAuthenticationProviderTest {

    private UserDetailsServiceAdapter userDetailsService;
    private PasswordEncoder passwordEncoder;
    private CustomAuthenticationProvider provider;

    @BeforeEach
    void setUp() {
        userDetailsService = mock(UserDetailsServiceAdapter.class);
        passwordEncoder = mock(PasswordEncoder.class);
        provider = new CustomAuthenticationProvider(userDetailsService, passwordEncoder);
    }

    @Test
    void shouldAuthenticateSuccessfully() {
        // Arrange
        String username = "testUser";
        String rawPassword = "password";
        String encodedPassword = "hashed";

        UserDetails userDetails = new User(username, encodedPassword, Collections.emptyList());
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(username, rawPassword);

        // Act
        var result = provider.authenticate(authRequest);

        // Assert
        assertNotNull(result, "Authentication result should not be null");
        assertEquals(username, result.getPrincipal(), "Principal should match username");
        assertTrue(result.getAuthorities().isEmpty(), "Authorities should be empty");
    }

    @Test
    void shouldThrowExceptionForInvalidPassword() {
        String username = "user";
        String rawPassword = "wrong";
        String encodedPassword = "hashed";

        UserDetails userDetails = new User(username, encodedPassword, Collections.emptyList());
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(username, rawPassword);

        assertThrows(BadCredentialsException.class, () -> provider.authenticate(authRequest));
    }

    @Test
    void shouldThrowExceptionForBlankCredentials() {
        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken("  ", "");

        assertThrows(BadCredentialsException.class, () -> provider.authenticate(authRequest));
    }

    @Test
    void shouldSupportUsernamePasswordAuthenticationToken() {
        assertTrue(provider.supports(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void shouldNotSupportOtherAuthenticationClasses() {
        assertFalse(provider.supports(Object.class));
    }
}
