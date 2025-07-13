package com.zerotrust.auth_gateway.application.implementations;

import com.zerotrust.auth_gateway.domain.enums.Role;
import com.zerotrust.auth_gateway.domain.exception.InvalidEmailException;
import com.zerotrust.auth_gateway.domain.exception.InvalidPasswordException;
import com.zerotrust.auth_gateway.domain.exception.InvalidUsernameException;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.application.usecase.implementations.UserRegistrationUseImpl;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.domain.service.EmailService;
import com.zerotrust.auth_gateway.domain.service.TOTPService;
import com.zerotrust.auth_gateway.infrastructure.security.jwt.JwtTokenGenerator;
import com.zerotrust.auth_gateway.application.dto.request.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserRegistrationUseImplTest {

    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private TOTPService totpService;
    private JwtTokenGenerator jwtTokenGenerator;
    private EmailService emailService;
    private UserRegistrationUseImpl registerUserUseCaseImpl;

    @BeforeEach
    void setUp() {
        passwordEncoder = mock(PasswordEncoder.class);
        userRepository = mock(UserRepository.class);
        totpService = mock(TOTPService.class);
        jwtTokenGenerator = mock(JwtTokenGenerator.class);
        emailService = mock(EmailService.class);
        registerUserUseCaseImpl = new UserRegistrationUseImpl(passwordEncoder, userRepository, totpService, jwtTokenGenerator, emailService);
    }

    @Test
    void shouldRegisterUserWithHashedPassword() {
        String rawPassword = "Abcdef1@";
        String hashedPassword = "hashed_pass";

        RegisterRequest request = new RegisterRequest(
                "validUser",
                rawPassword,
                "user@example.com",
                List.of(),
                false,
                rawPassword,
                false
        );

        when(passwordEncoder.encode(rawPassword)).thenReturn(hashedPassword);
        when(jwtTokenGenerator.generateActivationToken(any(), any())).thenReturn("fake-token");

        assertDoesNotThrow(() -> registerUserUseCaseImpl.register(request));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        verify(emailService).sendActivationEmail(eq("user@example.com"), any(), any(), isNull());

        User savedUser = userCaptor.getValue();
        assertEquals("validUser", savedUser.getUsername());
        assertEquals(hashedPassword, savedUser.getPasswordHash());
        assertFalse(savedUser.isMfaEnabled());
        assertEquals(null, savedUser.getMfaSecret());
        assertFalse(savedUser.isEnabled());
        assertFalse(savedUser.isFirstAccessRequired());
        assertNotNull(savedUser.getId());
    }

    @Test
    void shouldThrowExceptionWhenCommandIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                registerUserUseCaseImpl.register(null)
        );
        assertEquals("No user found with provided email or username.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsInvalid() {
        RegisterRequest request = new RegisterRequest(
                "validUser",
                "12345678",
                "user@example.com",
                List.of(Role.ROLE_USER.name()),
                false,
                "12345678",
                false
        );
        assertThrows(InvalidPasswordException.class, () -> registerUserUseCaseImpl.register(request));
    }

    @Test
    void shouldThrowExceptionWhenUsernameIsNull() {
        RegisterRequest request = new RegisterRequest(
                null,
                "Password1@",
                "user@example.com",
                List.of(Role.ROLE_USER.name()),
                false,
                "Password1@",
                false
        );
        assertThrows(InvalidUsernameException.class, () -> registerUserUseCaseImpl.register(request));
    }

    @Test
    void shouldThrowExceptionWhenUsernameIsBlank() {
        RegisterRequest request = new RegisterRequest(
                "   ",
                "Password1@",
                "user@example.com",
                List.of(Role.ROLE_USER.name()),
                false,
                "Password1@",
                false
        );
        assertThrows(InvalidUsernameException.class, () -> registerUserUseCaseImpl.register(request));
    }

    @Test
    void shouldThrowExceptionWhenUsernameIsTooShort() {
        RegisterRequest request = new RegisterRequest(
                "ab",
                "Password1@",
                "user@example.com",
                List.of(Role.ROLE_USER.name()),
                false,
                "Password1@",
                false
        );
        assertThrows(InvalidUsernameException.class, () -> registerUserUseCaseImpl.register(request));
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsNull() {
        RegisterRequest request = new RegisterRequest(
                "validUser",
                null,
                "user@example.com",
                List.of(Role.ROLE_USER.name()),
                false,
                null,
                false
        );
        assertThrows(InvalidPasswordException.class, () -> registerUserUseCaseImpl.register(request));
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsBlank() {
        RegisterRequest request = new RegisterRequest(
                "validUser",
                "   ",
                "user@example.com",
                List.of(Role.ROLE_USER.name()),
                false,
                "   ",
                false
        );
        assertThrows(InvalidPasswordException.class, () -> registerUserUseCaseImpl.register(request));
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        RegisterRequest request = new RegisterRequest(
                "validUser",
                "Password1@",
                null,
                List.of(Role.ROLE_USER.name()),
                false,
                "Password1@",
                false
        );
        assertThrows(InvalidEmailException.class, () -> registerUserUseCaseImpl.register(request));
    }

    @Test
    void shouldThrowExceptionWhenEmailIsBlank() {
        RegisterRequest request = new RegisterRequest(
                "validUser",
                "Password1@",
                "  ",
                List.of(Role.ROLE_USER.name()),
                false,
                "Password1@",
                false
        );
        assertThrows(InvalidEmailException.class, () -> registerUserUseCaseImpl.register(request));
    }

    @Test
    void shouldRegisterUserWithCustomRoles() {
        String rawPassword = "Password@1234";
        String hashedPassword = "hashed_pass";
        List<String> roles = List.of("ROLE_ADMIN", "ROLE_USER");

        RegisterRequest request = new RegisterRequest(
                "validUser",
                rawPassword,
                "user@example.com",
                roles,
                false,
                rawPassword,
                false
        );

        when(passwordEncoder.encode(rawPassword)).thenReturn(hashedPassword);
        when(jwtTokenGenerator.generateActivationToken(any(), any())).thenReturn("fake-token");

        registerUserUseCaseImpl.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        verify(emailService).sendActivationEmail(eq("user@example.com"), any(), any(), isNull());

        User savedUser = userCaptor.getValue();
        assertEquals(roles, savedUser.getRoles());
    }

    @Test
    void shouldRegisterUserWithMfaEnabledTrue() {
        String rawPassword = "Abcdef1@";
        String hashedPassword = "hashed_pass";
        String secret = "test-secret";
        String qrCodeUrl = "qr-code-url";

        RegisterRequest request = new RegisterRequest(
                "validUser",
                rawPassword,
                "user@example.com",
                List.of(Role.ROLE_USER.name()),
                true,
                rawPassword,
                false
        );

        when(passwordEncoder.encode(rawPassword)).thenReturn(hashedPassword);
        when(totpService.generateSecret()).thenReturn(secret);
        when(totpService.generateQrCodeUrl("validUser", secret)).thenReturn(qrCodeUrl);
        when(jwtTokenGenerator.generateActivationToken(any(), any())).thenReturn("fake-token");

        registerUserUseCaseImpl.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        verify(emailService).sendActivationEmail(eq("user@example.com"), any(), any(), eq(qrCodeUrl));

        User savedUser = userCaptor.getValue();
        assertTrue(savedUser.isMfaEnabled());
        assertEquals(secret, savedUser.getMfaSecret());
    }
}
