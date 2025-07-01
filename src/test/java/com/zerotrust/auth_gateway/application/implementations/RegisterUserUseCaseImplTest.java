package com.zerotrust.auth_gateway.application.implementations;

import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.application.usecase.implementations.RegisterUserUseCaseImpl;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.web.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RegisterUserUseCaseImplTest {

    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private RegisterUserUseCaseImpl registerUserUseCaseImpl;

    @BeforeEach
    void setUp() {
        passwordEncoder = mock(PasswordEncoder.class);
        userRepository = mock(UserRepository.class);
        registerUserUseCaseImpl = new RegisterUserUseCaseImpl(passwordEncoder, userRepository);
    }

    @Test
    void shouldRegisterUserWithHashedPassword() {
        // Arrange
        String rawPassword = "12345678";
        String hashedPassword = "hashed_pass";
        RegisterRequest request = new RegisterRequest("username", rawPassword);

        when(passwordEncoder.encode(rawPassword)).thenReturn(hashedPassword);

        // Act
        registerUserUseCaseImpl.register(request);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("username", savedUser.getUsername());
        assertEquals(hashedPassword, savedUser.getPasswordHash());
        assertFalse(savedUser.isMfaEnabled());
        assertEquals("", savedUser.getMfaSecret());
        assertTrue(savedUser.isEnabled());
        assertNotNull(savedUser.getId());
    }

    @Test
    void shouldThrowExceptionWhenCommandIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                registerUserUseCaseImpl.register(null)
        );
        assertEquals("RegisterUserCommand cannot be null.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUsernameIsNull() {
        RegisterRequest request = new RegisterRequest(null, "password");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                registerUserUseCaseImpl.register(request)
        );
        assertEquals("Username cannot be null or blank.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUsernameIsBlank() {
        RegisterRequest request = new RegisterRequest("   ", "password");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                registerUserUseCaseImpl.register(request)
        );
        assertEquals("Username cannot be null or blank.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsNull() {
        RegisterRequest request = new RegisterRequest("username", null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                registerUserUseCaseImpl.register(request)
        );
        assertEquals("Password cannot be null or blank.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsBlank() {
        RegisterRequest request = new RegisterRequest("username", "   ");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                registerUserUseCaseImpl.register(request)
        );
        assertEquals("Password cannot be null or blank.", exception.getMessage());
    }

    @Test
    void shouldRegisterUserWithCustomRoles() {
        // Arrange
        String rawPassword = "password";
        String hashedPassword = "hashed_pass";
        List<String> roles = List.of("ROLE_ADMIN", "ROLE_USER");
        RegisterRequest request = new RegisterRequest("username", rawPassword, roles);

        when(passwordEncoder.encode(rawPassword)).thenReturn(hashedPassword);

        // Act
        registerUserUseCaseImpl.register(request);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(roles, savedUser.getRoles());
    }
}
