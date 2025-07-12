package com.zerotrust.auth_gateway.application.implementations;

import com.zerotrust.auth_gateway.application.dto.AuthenticationRequest;
import com.zerotrust.auth_gateway.application.usecase.implementations.AuthServiceUseCaseImpl;
import com.zerotrust.auth_gateway.domain.exception.AuthenticationFailedException;
import com.zerotrust.auth_gateway.domain.exception.FirstAccessPasswordRequiredException;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.domain.service.TOTPService;
import com.zerotrust.auth_gateway.infrastructure.security.jwt.JwtTokenGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthServiceUseCaseImplTest {

    private AuthenticationManager authenticationManager;
    private JwtTokenGenerator jwtTokenGenerator;
    private UserRepository userRepository;
    private TOTPService totpService;
    private AuthServiceUseCaseImpl authService;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        jwtTokenGenerator = mock(JwtTokenGenerator.class);
        userRepository = mock(UserRepository.class);
        totpService = mock(TOTPService.class);

        authService = new AuthServiceUseCaseImpl(authenticationManager, jwtTokenGenerator, userRepository, totpService);
    }

    @Test
    void shouldLoginSuccessfullyWithoutMfa() {
        String username = "user";
        String password = "pass";
        User user = new User(UUID.randomUUID(), username, "hash", "email@test.com", false, "", true, List.of("ROLE_USER"), false);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        AuthenticationRequest request = new AuthenticationRequest(username, "user@example.com", password, null);

        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(auth.getAuthorities()).thenReturn((Collection) List.of(new SimpleGrantedAuthority("ROLE_USER")));
        when(jwtTokenGenerator.generateToken(username, List.of("ROLE_USER"))).thenReturn("jwt-token");

        String token = authService.login(request);

        assertEquals("jwt-token", token);
    }

    @Test
    void shouldLoginWithMfaSuccessfully() {
        String username = "mfaUser";
        String password = "pass";
        String otp = "123456";
        User user = new User(UUID.randomUUID(), username, "hash", "email@test.com", true, "secret", true, List.of("ROLE_USER"), false);
        AuthenticationRequest request = new AuthenticationRequest(username, "use@example.com", password, otp);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(totpService.verifyCode("secret", otp)).thenReturn(true);

        Authentication auth = mock(Authentication.class);
        when(auth.getAuthorities()).thenReturn((Collection) List.of(new SimpleGrantedAuthority("ROLE_USER")));
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtTokenGenerator.generateToken(username, List.of("ROLE_USER"))).thenReturn("jwt-token");

        String token = authService.login(request);

        assertEquals("jwt-token", token);
    }

    @Test
    void shouldThrowIfOtpMissingWhenMfaEnabled() {
        String username = "mfaUser";
        String password = "pass";
        User user = new User(UUID.randomUUID(), username, "hash", "email@test.com", true, "secret", true, List.of("ROLE_USER"), false);
        AuthenticationRequest request = new AuthenticationRequest(username, "user@example", password, null);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Exception ex = assertThrows(AuthenticationFailedException.class, () -> authService.login(request));
        assertEquals("OTP is required for MFA-enabled accounts.", ex.getMessage());
    }

    @Test
    void shouldThrowIfOtpInvalid() {
        String username = "mfaUser";
        String password = "pass";
        String otp = "000000";
        User user = new User(UUID.randomUUID(), username, "hash", "email@test.com", true, "secret", true, List.of("ROLE_USER"), false);
        AuthenticationRequest request = new AuthenticationRequest(username, "user@example.com", password, otp);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(totpService.verifyCode("secret", otp)).thenReturn(false);

        Exception ex = assertThrows(AuthenticationFailedException.class, () -> authService.login(request));
        assertEquals("Invalid OTP code.", ex.getMessage());
    }

    @Test
    void shouldThrowIfUserNotFound() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        AuthenticationRequest request = new AuthenticationRequest("user", null, null, null);

        Exception ex = assertThrows(AuthenticationFailedException.class, () -> authService.login(request));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void shouldThrowIfUserIsNotActivated() {
        AuthenticationRequest request = new AuthenticationRequest("user", null, "123456", null);
        User user = new User(UUID.randomUUID(), "user", "hash", "email@test.com", false, "", false, List.of("ROLE_USER"), false);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        Exception ex = assertThrows(AuthenticationFailedException.class, () -> authService.login(request));
        assertEquals("User account is not activated.", ex.getMessage());
    }

    @Test
    void shouldThrowIfFirstAccessRequired() {
        AuthenticationRequest request = new AuthenticationRequest("user", null, "123456", null);
        User user = new User(UUID.randomUUID(), "user", "hash", "email@test.com", false, "", true, List.of("ROLE_USER"), true);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        Exception ex = assertThrows(FirstAccessPasswordRequiredException.class, () -> authService.login(request));
        assertEquals("You must reset your password before logging in for the first time.", ex.getMessage());
    }

    @Test
    void shouldThrowIfPasswordBlank() {
        AuthenticationRequest request = new AuthenticationRequest("user", null, "   ", null);
        User user = new User(UUID.randomUUID(), "user", "hash", "email@test.com", false, "", true, List.of("ROLE_USER"), false);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        Exception ex = assertThrows(AuthenticationFailedException.class, () -> authService.login(request));
        assertEquals("Invalid password.", ex.getMessage());
    }
}
