package com.zerotrust.auth_gateway.infrastructure.config;

import com.zerotrust.auth_gateway.application.service.interfaces.JwtTokenService;
import com.zerotrust.auth_gateway.application.usecase.implementations.registration.PublicRegistrationUseCaseImpl;
import com.zerotrust.auth_gateway.application.usecase.interfaces.auth.MfaManagementUseCase;
import com.zerotrust.auth_gateway.application.usecase.interfaces.registration.PublicRegistrationUseCase;
import com.zerotrust.auth_gateway.domain.enums.Role;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.domain.service.interfaces.EmailService;
import com.zerotrust.auth_gateway.infrastructure.config.core.AdminProperties;
import com.zerotrust.auth_gateway.infrastructure.config.core.UseCaseConfig;
import com.zerotrust.auth_gateway.infrastructure.seed.LoginPolicySeeder;
import com.zerotrust.auth_gateway.infrastructure.seed.RoleSeeder;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UseCaseConfigTest {

    @Test
    void shouldCreateRegisterUserUseCaseBean() {
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        UserRepository userRepository = mock(UserRepository.class);
        MfaManagementUseCase mfaManagementUseCase = mock(MfaManagementUseCase.class);
        EmailService emailService = mock(EmailService.class);
        JwtTokenService jwtTokenService = mock(JwtTokenService.class);

        UseCaseConfig config = new UseCaseConfig();

        PublicRegistrationUseCase useCase = config.registerUserUseCase(
                passwordEncoder,
                userRepository,
                mfaManagementUseCase,
                emailService,
                jwtTokenService
        );

        assertNotNull(useCase);
        assertEquals(PublicRegistrationUseCaseImpl.class, useCase.getClass());
    }

    @Test
    void shouldRunRoleSeederAndCreateAdminUserIfNotExists() throws Exception {
        AdminProperties adminProperties = mock(AdminProperties.class);
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        RoleSeeder roleSeeder = mock(RoleSeeder.class);
        LoginPolicySeeder loginPolicySeeder = mock(LoginPolicySeeder.class);

        when(adminProperties.getUsername()).thenReturn("admin");
        when(adminProperties.getPassword()).thenReturn("pass");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");

        UseCaseConfig config = new UseCaseConfig();

        CommandLineRunner runner = config.createAdminUser(adminProperties, userRepository, passwordEncoder, roleSeeder, loginPolicySeeder);
        runner.run();

        verify(roleSeeder).run();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("admin", savedUser.getUsername());
        assertEquals("encodedPass", savedUser.getPasswordHash());
        assertTrue(savedUser.isEnabled());
        assertFalse(savedUser.isMfaEnabled());
        assertNull(savedUser.getMfaSecret());
        assertEquals(List.of(Role.ROLE_ADMIN.name()), savedUser.getRoles());
    }

    @Test
    void shouldNotCreateAdminUserIfAlreadyExists() throws Exception {
        AdminProperties adminProperties = mock(AdminProperties.class);
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        RoleSeeder roleSeeder = mock(RoleSeeder.class);
        LoginPolicySeeder loginPolicySeeder = mock(LoginPolicySeeder.class);

        when(adminProperties.getUsername()).thenReturn("admin");
        when(userRepository.findByUsername("admin")).thenReturn(
                Optional.of(
                        new User(
                                UUID.randomUUID(),
                                "admin",
                                "hash",
                                "admin@example.com",
                                false,
                                null,
                                true,
                                List.of(Role.ROLE_ADMIN.name()),
                                false
                        )
                )
        );

        UseCaseConfig config = new UseCaseConfig();

        CommandLineRunner runner = config.createAdminUser(adminProperties, userRepository, passwordEncoder, roleSeeder, loginPolicySeeder);
        runner.run();

        verify(roleSeeder).run();
        verify(userRepository, never()).save(any());
    }
}
