package com.zerotrust.auth_gateway.infrastructure.config;

import com.zerotrust.auth_gateway.application.usecase.RegisterUserUseCase;
import com.zerotrust.auth_gateway.application.repository.UserRepository;
import com.zerotrust.auth_gateway.application.service.RegisterUserService;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

public class UseCaseConfigTest {

    @Test
    void shouldCreateRegisterUserUseCaseBean() {
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        UserRepository userRepository = mock(UserRepository.class);

        UseCaseConfig config = new UseCaseConfig();
        RegisterUserUseCase useCase = config.registerUserUseCase(passwordEncoder, userRepository);

        assertNotNull(useCase, "RegisterUserUseCase bean should not be null");
        assertEquals(RegisterUserService.class, useCase.getClass(), "Bean should be an instance of RegisterUserService");
    }
}
