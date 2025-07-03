package com.zerotrust.auth_gateway.application.implementations;

import com.zerotrust.auth_gateway.domain.exception.InvalidPasswordException;
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
        String rawPassword = "Abcdef1@"; // senha válida, respeitando as regras
        String hashedPassword = "hashed_pass";
        RegisterRequest request = new RegisterRequest("username", rawPassword);

        when(passwordEncoder.encode(rawPassword)).thenReturn(hashedPassword);

        // Act & Assert
        assertDoesNotThrow(() -> registerUserUseCaseImpl.register(request));

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
    void shouldThrowExceptionWhenPasswordIsInvalid() {
        RegisterRequest request = new RegisterRequest("username", "12345678");
        InvalidPasswordException ex = assertThrows(InvalidPasswordException.class, () -> registerUserUseCaseImpl.register(request));
        assertTrue(ex.getMessage().contains("uppercase"));
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
        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () -> registerUserUseCaseImpl.register(request));
        assertTrue(exception.getMessage().contains("at least"));
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsBlank() {
        RegisterRequest request = new RegisterRequest("username", "   ");
        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () -> registerUserUseCaseImpl.register(request));
        assertTrue(exception.getMessage().contains("at least"));
    }

    @Test
    void shouldRegisterUserWithCustomRoles() {
        // Arrange
        String rawPassword = "password@1234Test";
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
