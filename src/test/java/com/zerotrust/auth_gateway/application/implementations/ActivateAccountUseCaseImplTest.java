package com.zerotrust.auth_gateway.application.implementations;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.zerotrust.auth_gateway.application.usecase.implementations.ActivateAccountUseCaseImpl;
import com.zerotrust.auth_gateway.domain.exception.FirstAccessPasswordRequiredException;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.infrastructure.security.jwt.JwtTokenGenerator;
import com.zerotrust.auth_gateway.application.dto.request.PasswordResetRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ActivateAccountUseCaseImplTest {

    private JwtTokenGenerator jwtTokenGenerator;
    private UserRepository userRepository;
    private ActivateAccountUseCaseImpl activateAccountUseCase;

    @BeforeEach
    void setUp() {
        jwtTokenGenerator = mock(JwtTokenGenerator.class);
        userRepository = mock(UserRepository.class);
        activateAccountUseCase = new ActivateAccountUseCaseImpl(jwtTokenGenerator, userRepository);
    }

    @Test
    void shouldActivateUserSuccessfully_whenFirstAccessIsFalse() {
        String username = "user";
        String token = "token";
        DecodedJWT decodedJWT = mock(DecodedJWT.class);

        when(decodedJWT.getSubject()).thenReturn(username);
        when(jwtTokenGenerator.verifyToken(token)).thenReturn(decodedJWT);

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
        when(jwtTokenGenerator.verifyToken(token)).thenReturn(decodedJWT);

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
        when(jwtTokenGenerator.verifyToken(token)).thenReturn(decodedJWT);

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
        when(jwtTokenGenerator.verifyToken(token)).thenReturn(decodedJWT);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                activateAccountUseCase.activate(token, null)
        );

        assertEquals("User not found.", exception.getMessage());
    }
}
