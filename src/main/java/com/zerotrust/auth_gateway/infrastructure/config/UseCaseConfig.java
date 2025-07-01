package com.zerotrust.auth_gateway.infrastructure.config;

import com.zerotrust.auth_gateway.application.usecase.interfaces.RegisterUserUseCase;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.application.usecase.implementations.RegisterUserUseCaseImpl;
import com.zerotrust.auth_gateway.domain.enums.Role;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.infrastructure.seed.RoleSeeder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Configuration
@EnableConfigurationProperties(AdminProperties.class)
public class UseCaseConfig {

    @Bean
    public RegisterUserUseCase registerUserUseCase(PasswordEncoder passwordEncoder, UserRepository useRepositoryPort) {
        return new RegisterUserUseCaseImpl(passwordEncoder, useRepositoryPort);
    }

    @Bean
    public CommandLineRunner createAdminUser(AdminProperties adminProperties, UserRepository userRepository, PasswordEncoder passwordEncoder, RoleSeeder roleSeeder) {
        return args -> {
            roleSeeder.run();

            Optional<User> existingAdmin = userRepository.findByUsername(adminProperties.getUsername());
            if (existingAdmin.isEmpty()) {
                User admin = new User(
                        UUID.randomUUID(),
                        adminProperties.getUsername(),
                        passwordEncoder.encode(adminProperties.getPassword()),
                        false,
                        null,
                        true,
                        List.of(Role.ROLE_ADMIN.name())
                );

                userRepository.save(admin);
            }
        };
    }
}
