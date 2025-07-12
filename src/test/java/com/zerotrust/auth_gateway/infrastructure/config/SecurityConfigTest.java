package com.zerotrust.auth_gateway.infrastructure.config;

import com.zerotrust.auth_gateway.application.service.interfaces.LoginAttemptService;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.domain.service.TOTPService;
import com.zerotrust.auth_gateway.infrastructure.security.filter.JwtAuthenticationFilter;
import com.zerotrust.auth_gateway.infrastructure.security.jwt.JwtTokenGenerator;
import com.zerotrust.auth_gateway.infrastructure.security.providers.CustomAuthenticationProvider;
import com.zerotrust.auth_gateway.infrastructure.security.userdetails.UserDetailsServiceAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class SecurityConfigTest {

    private final SecurityConfig config = new SecurityConfig();

    @Test
    void shouldReturnPasswordEncoder() {
        PasswordEncoder encoder = config.passwordEncoder();
        assertNotNull(encoder);
        assertTrue(encoder.matches("password", encoder.encode("password")));
    }

    @Test
    void shouldReturnAuthenticationProvider() {
        UserDetailsServiceAdapter adapter = mock(UserDetailsServiceAdapter.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);

        AuthenticationProvider provider = config.authenticationProvider(adapter, encoder);

        assertNotNull(provider);
        assertInstanceOf(CustomAuthenticationProvider.class, provider);
    }

    @Test
    void shouldReturnAuthenticationManager() throws Exception {
        AuthenticationConfiguration configuration = mock(AuthenticationConfiguration.class);
        AuthenticationManager mockManager = mock(AuthenticationManager.class);

        when(configuration.getAuthenticationManager()).thenReturn(mockManager);

        AuthenticationManager result = config.authenticationManager(configuration);

        assertNotNull(result);
        assertEquals(mockManager, result);
    }

    @Test
    void shouldCreateJwtAuthenticationFilter() {
        JwtTokenGenerator generator = mock(JwtTokenGenerator.class);
        assertNotNull(config.jwtAuthenticationFilter(generator));
    }

    @Test
    void shouldCreateAuthServiceUseCase() {
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        JwtTokenGenerator jwtTokenGenerator = mock(JwtTokenGenerator.class);
        UserRepository userRepository = mock(UserRepository.class);
        TOTPService totpService = mock(TOTPService.class);
        LoginAttemptService loginAttemptService = mock(LoginAttemptService.class);

        assertNotNull(config.authServiceUseCase(authenticationManager, jwtTokenGenerator, userRepository, totpService, loginAttemptService));
    }

    @Test
    void shouldCreateTOTPService() {
        assertNotNull(config.totpService());
    }

    @Test
    void shouldConfigureSecurityFilterChain() throws Exception {
        HttpSecurity http = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);
        AuthenticationProvider authProvider = mock(AuthenticationProvider.class);
        JwtAuthenticationFilter jwtFilter = mock(JwtAuthenticationFilter.class);

        when(http.csrf(any())).thenReturn(http);
        when(http.headers(any())).thenReturn(http);
        when(http.authorizeHttpRequests(any())).thenReturn(http);
        when(http.authenticationProvider(authProvider)).thenReturn(http);
        when(http.addFilterBefore(eq(jwtFilter), any())).thenReturn(http);
        when(http.sessionManagement(any())).thenReturn(http);
        when(http.exceptionHandling(any())).thenReturn(http);

        SecurityFilterChain chain = config.securityFilterChain(http, authProvider, jwtFilter);

        assertNotNull(chain);
        verify(http).authenticationProvider(authProvider);
        verify(http).addFilterBefore(eq(jwtFilter), any());
    }
}
