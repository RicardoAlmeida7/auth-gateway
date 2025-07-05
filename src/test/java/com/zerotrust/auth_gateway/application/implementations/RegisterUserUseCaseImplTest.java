package com.zerotrust.auth_gateway.application.implementations;

import com.zerotrust.auth_gateway.domain.exception.InvalidEmailException;
import com.zerotrust.auth_gateway.domain.exception.InvalidPasswordException;
import com.zerotrust.auth_gateway.domain.exception.InvalidUsernameException;
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
        String rawPassword = "Abcdef1@";
        String hashedPassword = "hashed_pass";
        RegisterRequest request = new RegisterRequest("validUser", rawPassword, "user@example.com", List.of());

        when(passwordEncoder.encode(rawPassword)).thenReturn(hashedPassword);

        assertDoesNotThrow(() -> registerUserUseCaseImpl.register(request));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("validUser", savedUser.getUsername());
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
        RegisterRequest request = new RegisterRequest("validUser", "12345678", "user@example.com", List.of());
        InvalidPasswordException ex = assertThrows(InvalidPasswordException.class, () -> registerUserUseCaseImpl.register(request));
        assertTrue(ex.getMessage().contains("uppercase"));
    }

    @Test
    void shouldThrowExceptionWhenUsernameIsNull() {
        RegisterRequest request = new RegisterRequest(null, "Password1@", "user@example.com", List.of());
        InvalidUsernameException exception = assertThrows(InvalidUsernameException.class, () ->
                registerUserUseCaseImpl.register(request)
        );
        assertEquals("Username must not be null or blank.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUsernameIsBlank() {
        RegisterRequest request = new RegisterRequest("   ", "Password1@", "user@example.com", List.of());
        InvalidUsernameException exception = assertThrows(InvalidUsernameException.class, () ->
                registerUserUseCaseImpl.register(request)
        );
        assertEquals("Username must not be null or blank.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUsernameIsTooShort() {
        RegisterRequest request = new RegisterRequest("ab", "Password1@", "user@example.com", List.of());
        InvalidUsernameException exception = assertThrows(InvalidUsernameException.class, () ->
                registerUserUseCaseImpl.register(request)
        );
        assertEquals("Username must be between 3 and 30 characters.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsNull() {
        RegisterRequest request = new RegisterRequest("validUser", null, "user@example.com", List.of());
        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () -> registerUserUseCaseImpl.register(request));
        assertTrue(exception.getMessage().contains("at least"));
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsBlank() {
        RegisterRequest request = new RegisterRequest("validUser", "   ", "user@example.com", List.of());
        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () -> registerUserUseCaseImpl.register(request));
        assertTrue(exception.getMessage().contains("at least"));
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        RegisterRequest request = new RegisterRequest("validUser", "Password1@", null, List.of());
        InvalidEmailException exception = assertThrows(InvalidEmailException.class, () -> registerUserUseCaseImpl.register(request));
        assertEquals("Email must not be null or blank.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsBlank() {
        RegisterRequest request = new RegisterRequest("validUser", "Password1@", "  ", List.of());
        InvalidEmailException exception = assertThrows(InvalidEmailException.class, () -> registerUserUseCaseImpl.register(request));
        assertEquals("Email must not be null or blank.", exception.getMessage());
    }

    @Test
    void shouldRegisterUserWithCustomRoles() {
        String rawPassword = "Password@1234";
        String hashedPassword = "hashed_pass";
        List<String> roles = List.of("ROLE_ADMIN", "ROLE_USER");
        RegisterRequest request = new RegisterRequest("validUser", rawPassword, "user@example.com", roles);

        when(passwordEncoder.encode(rawPassword)).thenReturn(hashedPassword);

        registerUserUseCaseImpl.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(roles, savedUser.getRoles());
    }
}
