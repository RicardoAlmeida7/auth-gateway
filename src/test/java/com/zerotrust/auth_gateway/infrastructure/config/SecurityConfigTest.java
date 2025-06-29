package com.zerotrust.auth_gateway.infrastructure.config;

import com.zerotrust.auth_gateway.infrastructure.security.jwt.JwtTokenGenerator;
import com.zerotrust.auth_gateway.infrastructure.security.providers.CustomAuthenticationProvider;
import com.zerotrust.auth_gateway.infrastructure.security.userdetails.UserDetailsServiceAdapter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@SpringBootTest
public class SecurityConfigTest {

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Test
    void shouldCreateBeansProperly() throws Exception {
        // Mock dependencies required by the SecurityConfig class
        UserDetailsServiceAdapter userDetailsServiceAdapter = mock(UserDetailsServiceAdapter.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        AuthenticationConfiguration authenticationConfiguration = mock(AuthenticationConfiguration.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);

        // Configure mock to return the mocked AuthenticationManager
        Mockito.when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);

        // Instantiate the SecurityConfig class
        SecurityConfig config = new SecurityConfig();

        // Test PasswordEncoder bean creation and basic encode/match functionality
        PasswordEncoder encoder = config.passwordEncoder();
        assertNotNull(encoder);
        assertTrue(encoder.matches("test", encoder.encode("test")));

        // Test AuthenticationProvider bean creation and type correctness
        AuthenticationProvider provider = config.authenticationProvider(userDetailsServiceAdapter, passwordEncoder);
        assertNotNull(provider);
        assertInstanceOf(CustomAuthenticationProvider.class, provider);

        // Test AuthenticationManager bean creation returns the mocked instance
        AuthenticationManager manager = config.authenticationManager(authenticationConfiguration);
        assertNotNull(manager);
        assertEquals(authenticationManager, manager);

        // Mock HttpSecurity to simulate Spring Security DSL fluent API
        var httpSecurity = Mockito.mock(org.springframework.security.config.annotation.web.builders.HttpSecurity.class,
                invocation -> {
                    // For fluent methods that return HttpSecurity, return the mock itself
                    if (invocation.getMethod().getReturnType().equals(org.springframework.security.config.annotation.web.builders.HttpSecurity.class)) {
                        return invocation.getMock();
                    }
                    return Mockito.RETURNS_DEFAULTS.answer(invocation);
                });
    }

    @Test
    void securityFilterChainIsCreated() {
        assertNotNull(securityFilterChain);
    }
}
