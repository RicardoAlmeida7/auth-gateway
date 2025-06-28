package com.zerotrust.auth_gateway.infrastructure.config;

import com.zerotrust.auth_gateway.application.port.in.RegisterUserUseCase;
import com.zerotrust.auth_gateway.application.port.out.UserRepositoryPort;
import com.zerotrust.auth_gateway.application.service.RegisterUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UseCaseConfig {

    @Bean
    public RegisterUserUseCase registerUserUseCase(PasswordEncoder passwordEncoder, UserRepositoryPort useRepositoryPort) {
        return new RegisterUserService(passwordEncoder, useRepositoryPort);
    }
}
