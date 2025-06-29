package com.zerotrust.auth_gateway.infrastructure.config;

import com.zerotrust.auth_gateway.application.usecase.RegisterUserUseCase;
import com.zerotrust.auth_gateway.application.repository.UserRepository;
import com.zerotrust.auth_gateway.application.service.RegisterUserService;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.infrastructure.repository.entities.RoleEntity;
import com.zerotrust.auth_gateway.infrastructure.repository.repositories.interfaces.JpaRoleRepository;
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
        return new RegisterUserService(passwordEncoder, useRepositoryPort);
    }

    @Bean
    public CommandLineRunner createAdminUser(AdminProperties adminProperties, UserRepository userRepository, PasswordEncoder passwordEncoder, JpaRoleRepository roleRepository) {
        return args -> {

            if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
                roleRepository.save(new RoleEntity(null, "ROLE_ADMIN"));
            }
            if (roleRepository.findByName("ROLE_USER").isEmpty()) {
                roleRepository.save(new RoleEntity(null, "ROLE_USER"));
            }

            Optional<User> existingAdmin = userRepository.findByUsername(adminProperties.getUsername());
            if (existingAdmin.isEmpty()) {
                User admin = new User(
                        UUID.randomUUID(),
                        adminProperties.getUsername(),
                        passwordEncoder.encode(adminProperties.getPassword()),
                        false,
                        null,
                        true,
                        List.of("ROLE_ADMIN")
                );

                userRepository.save(admin);
            }
        };
    }
}
