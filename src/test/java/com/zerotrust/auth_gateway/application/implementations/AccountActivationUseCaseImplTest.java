package com.zerotrust.auth_gateway.application.implementations;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.zerotrust.auth_gateway.application.dto.request.password.PasswordResetRequest;
import com.zerotrust.auth_gateway.application.service.interfaces.JwtTokenService;
import com.zerotrust.auth_gateway.application.usecase.implementations.activation.AccountActivationUseCaseImpl;
import com.zerotrust.auth_gateway.domain.exception.FirstAccessPasswordRequiredException;
import com.zerotrust.auth_gateway.domain.exception.UserNotFoundException;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountActivationUseCaseImplTest {

    private UserRepository userRepository;
    private AccountActivationUseCaseImpl activateAccountUseCase;
    private JwtTokenService jwtTokenService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        jwtTokenService = mock(JwtTokenService.class);
        activateAccountUseCase = new AccountActivationUseCaseImpl(userRepository, passwordEncoder, jwtTokenService);
    }

    @Test
    void shouldActivateUserSuccessfully_whenFirstAccessIsFalse() {
        String username = "user";
        String token = "token";
        DecodedJWT decodedJWT = mock(DecodedJWT.class);

        when(decodedJWT.getSubject()).thenReturn(username);
        when(jwtTokenService.validateActivationToken(token)).thenReturn(username);

        User user = new User(UUID.randomUUID(), username, "hashed", "email@example.com", false, "", false, null, false);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        activateAccountUseCase.activate(token, null);

        assertTrue(user.isEnabled());
        verify(userRepository).save(user);
    }

    @Test
    void shouldActivateUserSuccessfully_whenFirstAccessIsTrueWithValidPasswords() {
        String username = "user";
        String token = "token";
        DecodedJWT decodedJWT = mock(DecodedJWT.class);

        when(decodedJWT.getSubject()).thenReturn(username);
        when(jwtTokenService.validateActivationToken(token)).thenReturn(username);

        User user = new User(UUID.randomUUID(), username, "hashed", "email@example.com", true, "", false, null, false);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        PasswordResetRequest request = new PasswordResetRequest("NewPassword123", "NewPassword123");

        activateAccountUseCase.activate(token, request);

        assertTrue(user.isEnabled());
        assertFalse(user.isFirstAccessRequired());
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowException_whenFirstAccessIsTrueAndRequestIsNull() {
        String username = "user";
        String token = "token";
        DecodedJWT decodedJWT = mock(DecodedJWT.class);

        when(decodedJWT.getSubject()).thenReturn(username);
        when(jwtTokenService.validateActivationToken(token)).thenReturn(username);

        User user = new User(UUID.randomUUID(), username, "hashed", "email@example.com", true, "", false, null, true);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        FirstAccessPasswordRequiredException exception = assertThrows(FirstAccessPasswordRequiredException.class, () ->
                activateAccountUseCase.activate(token, null)
        );

        assertEquals("Password reset is required on first access. Please provide a new password and confirmation.", exception.getMessage());
        verify(userRepository, never()).save(user);
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        String token = "token";
        String username = "nonexistent_user";
        DecodedJWT decodedJWT = mock(DecodedJWT.class);

        when(decodedJWT.getSubject()).thenReturn(username);
        when(jwtTokenService.validateActivationToken(token)).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                activateAccountUseCase.activate(token, null)
        );

        assertEquals("User not found.", exception.getMessage());
    }
}
