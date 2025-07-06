package com.zerotrust.auth_gateway.application.implementations;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.zerotrust.auth_gateway.application.usecase.implementations.ActivateAccountUseCaseImpl;
import com.zerotrust.auth_gateway.domain.enums.Role;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.infrastructure.security.jwt.JwtTokenGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
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
    void shouldActivateUserSuccessfully() {
        // Arrange
        String username = "user";
        String token = jwtTokenGenerator.generateToken(username, List.of(Role.ROLE_USER.name()));

        DecodedJWT decodedJWT = mock(DecodedJWT.class);
        when(decodedJWT.getSubject()).thenReturn(username);
        when(jwtTokenGenerator.verifyToken(token)).thenReturn(decodedJWT);

        User user = new User(UUID.randomUUID(), username, "hashed", "email@example.com", false, "", false, null);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        activateAccountUseCase.activate(token);

        // Assert
        assertTrue(user.isEnabled());
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        // Arrange
        String token = "valid.token.here";
        String username = "nonexistent_user";

        DecodedJWT decodedJWT = mock(DecodedJWT.class);
        when(decodedJWT.getSubject()).thenReturn(username);
        when(jwtTokenGenerator.verifyToken(token)).thenReturn(decodedJWT);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                activateAccountUseCase.activate(token)
        );
        assertEquals("User not found.", exception.getMessage());
    }
}
