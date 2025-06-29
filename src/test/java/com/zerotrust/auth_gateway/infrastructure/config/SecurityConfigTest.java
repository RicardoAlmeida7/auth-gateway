package com.zerotrust.auth_gateway.infrastructure.config;

import com.zerotrust.auth_gateway.infrastructure.security.jwt.JwtTokenGenerator;
import com.zerotrust.auth_gateway.infrastructure.security.providers.CustomAuthenticationProvider;
import com.zerotrust.auth_gateway.infrastructure.security.userdetails.UserDetailsServiceAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import(SecurityConfig.class) // Garante que a configuração seja aplicada
public class SecurityConfigTest {

    private final SecurityConfig config = new SecurityConfig();

    @Test
    void shouldReturnPasswordEncoder() {
        PasswordEncoder encoder = config.passwordEncoder();
        assertNotNull(encoder);
        assertTrue(encoder.matches("senha123", encoder.encode("senha123")));
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
}
