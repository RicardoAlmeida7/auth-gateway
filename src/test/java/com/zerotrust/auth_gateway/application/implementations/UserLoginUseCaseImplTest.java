package com.zerotrust.auth_gateway.application.implementations;

import com.zerotrust.auth_gateway.application.dto.request.AuthenticationRequest;
import com.zerotrust.auth_gateway.application.dto.response.JwtResponse;
import com.zerotrust.auth_gateway.application.service.interfaces.JwtTokenService;
import com.zerotrust.auth_gateway.application.service.interfaces.LoginAttemptService;
import com.zerotrust.auth_gateway.application.usecase.implementations.UserLoginUseCaseImpl;
import com.zerotrust.auth_gateway.domain.exception.AuthenticationFailedException;
import com.zerotrust.auth_gateway.domain.exception.FirstAccessPasswordRequiredException;
import com.zerotrust.auth_gateway.domain.exception.UserNotFoundException;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.domain.service.TOTPService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserLoginUseCaseImplTest {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private TOTPService totpService;
    private UserLoginUseCaseImpl userLoginUseCase;
    private LoginAttemptService loginAttemptService;
    private JwtTokenService jwtTokenService;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        userRepository = mock(UserRepository.class);
        totpService = mock(TOTPService.class);
        loginAttemptService = mock(LoginAttemptService.class);
        jwtTokenService = mock(JwtTokenService.class);

        userLoginUseCase = new UserLoginUseCaseImpl(
                authenticationManager,
                userRepository,
                totpService,
                loginAttemptService,
                jwtTokenService
        );
    }

    @Test
    void shouldLoginSuccessfullyWithoutMfa() {
        String userId = "user";
        String password = "pass";
        User user = new User(UUID.randomUUID(), userId, "hash", "email@test.com", false, "", true, List.of("ROLE_USER"), false);

        when(userRepository.findByUsername(userId)).thenReturn(Optional.of(user));
        AuthenticationRequest request = new AuthenticationRequest(userId, password, null);

        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtTokenService.generateAuthToken(user)).thenReturn(new JwtResponse("jwt-access-token", "jwt-refresh-token"));

        JwtResponse token = userLoginUseCase.login(request);

        assertEquals("jwt-access-token", token.accessToken());
    }

    @Test
    void shouldLoginWithMfaSuccessfully() {
        String userId = "mfaUser";
        String password = "pass";
        String otp = "123456";
        User user = new User(UUID.randomUUID(), userId, "hash", "email@test.com", true, "secret", true, List.of("ROLE_USER"), false);
        AuthenticationRequest request = new AuthenticationRequest(userId, password, otp);

        when(userRepository.findByUsername(userId)).thenReturn(Optional.of(user));
        when(totpService.verifyCode("secret", otp)).thenReturn(true);

        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtTokenService.generateAuthToken(user)).thenReturn(new JwtResponse("jwt-access-token", "jwt-refresh-token"));

        JwtResponse token = userLoginUseCase.login(request);

        assertEquals("jwt-access-token", token.accessToken());
    }

    @Test
    void shouldThrowIfOtpMissingWhenMfaEnabled() {
        String userId = "mfaUser";
        String password = "pass";
        User user = new User(UUID.randomUUID(), userId, "hash", "email@test.com", true, "secret", true, List.of("ROLE_USER"), false);
        AuthenticationRequest request = new AuthenticationRequest(userId, password, null);

        when(userRepository.findByUsername(userId)).thenReturn(Optional.of(user));

        Exception ex = assertThrows(AuthenticationFailedException.class, () -> userLoginUseCase.login(request));
        assertEquals("OTP is required for MFA-enabled accounts.", ex.getMessage());
    }

    @Test
    void shouldThrowIfOtpInvalid() {
        String userId = "mfaUser";
        String password = "pass";
        String otp = "000000";
        User user = new User(UUID.randomUUID(), userId, "hash", "email@test.com", true, "secret", true, List.of("ROLE_USER"), false);
        AuthenticationRequest request = new AuthenticationRequest(userId, password, otp);

        when(userRepository.findByUsername(userId)).thenReturn(Optional.of(user));
        when(totpService.verifyCode("secret", otp)).thenReturn(false);

        Exception ex = assertThrows(AuthenticationFailedException.class, () -> userLoginUseCase.login(request));
        assertEquals("Invalid OTP code.", ex.getMessage());
    }

    @Test
    void shouldThrowIfUserNotFound() {
        String userId = "user";
        when(userRepository.findByUsername(userId)).thenReturn(Optional.empty());
        AuthenticationRequest request = new AuthenticationRequest(userId, null, null);

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> userLoginUseCase.login(request));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void shouldThrowIfUserIsNotActivated() {
        String userId = "user";
        AuthenticationRequest request = new AuthenticationRequest(userId, "123456", null);
        User user = new User(UUID.randomUUID(), userId, "hash", "email@test.com", false, "", false, List.of("ROLE_USER"), false);
        when(userRepository.findByUsername(userId)).thenReturn(Optional.of(user));

        Exception ex = assertThrows(AuthenticationFailedException.class, () -> userLoginUseCase.login(request));
        assertEquals("User account is not activated.", ex.getMessage());
    }

    @Test
    void shouldThrowIfFirstAccessRequired() {
        String userId = "user";
        AuthenticationRequest request = new AuthenticationRequest(userId, "123456", null);
        User user = new User(UUID.randomUUID(), userId, "hash", "email@test.com", false, "", true, List.of("ROLE_USER"), true);
        when(userRepository.findByUsername(userId)).thenReturn(Optional.of(user));

        Exception ex = assertThrows(FirstAccessPasswordRequiredException.class, () -> userLoginUseCase.login(request));
        assertEquals("You must reset your password before logging in for the first time.", ex.getMessage());
    }

    @Test
    void shouldThrowIfPasswordBlank() {
        String userId = "user";
        AuthenticationRequest request = new AuthenticationRequest(userId, "   ", null);
        User user = new User(UUID.randomUUID(), userId, "hash", "email@test.com", false, "", true, List.of("ROLE_USER"), false);
        when(userRepository.findByUsername(userId)).thenReturn(Optional.of(user));

        Exception ex = assertThrows(AuthenticationFailedException.class, () -> userLoginUseCase.login(request));
        assertEquals("Invalid password.", ex.getMessage());
    }
}
