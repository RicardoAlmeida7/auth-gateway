package com.zerotrust.auth_gateway.infrastructure.config;

import com.zerotrust.auth_gateway.application.service.interfaces.JwtTokenService;
import com.zerotrust.auth_gateway.application.usecase.implementations.ActivateAccountUseCaseImpl;
import com.zerotrust.auth_gateway.application.usecase.implementations.PasswordResetUseCaseImpl;
import com.zerotrust.auth_gateway.application.usecase.implementations.UserManagementUseCaseImpl;
import com.zerotrust.auth_gateway.application.usecase.implementations.UserRegistrationUseCaseImpl;
import com.zerotrust.auth_gateway.application.usecase.interfaces.*;
import com.zerotrust.auth_gateway.domain.enums.Role;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.domain.service.EmailService;
import com.zerotrust.auth_gateway.infrastructure.seed.LoginPolicySeeder;
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
    public UserRegistrationUseCase registerUserUseCase(
            PasswordEncoder passwordEncoder,
            UserRepository useRepositoryPort,
            MfaManagementUseCase mfaManagementUseCase,
            EmailService emailService,
            JwtTokenService jwtTokenService
    ) {

        return new UserRegistrationUseCaseImpl(
                passwordEncoder,
                useRepositoryPort,
                mfaManagementUseCase,
                emailService,
                jwtTokenService
        );
    }

    @Bean
    public ActivateAccountUseCase activateAccountUseCase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService
    ) {
        return new ActivateAccountUseCaseImpl(userRepository, passwordEncoder, jwtTokenService);
    }

    @Bean
    public PasswordResetUseCase passwordResetUseCase(
            UserRepository userRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService
    ) {
        return new PasswordResetUseCaseImpl(userRepository, emailService, passwordEncoder, jwtTokenService);
    }

    @Bean
    public CommandLineRunner createAdminUser(
            AdminProperties adminProperties,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            RoleSeeder roleSeeder,
            LoginPolicySeeder loginPolicySeeder
    ) {
        return args -> {
            roleSeeder.run();
            loginPolicySeeder.run();

            Optional<User> existingAdmin = userRepository.findByUsername(adminProperties.getUsername());
            if (existingAdmin.isEmpty()) {
                User admin = new User(
                        UUID.randomUUID(),
                        adminProperties.getUsername(),
                        passwordEncoder.encode(adminProperties.getPassword()),
                        adminProperties.getEmail(),
                        false,
                        null,
                        true,
                        List.of(Role.ROLE_ADMIN.name()),
                        false);

                userRepository.save(admin);
            }
        };
    }

    @Bean
    public UserManagementUseCase userManagementUseCase(
            UserRepository repository,
            MfaManagementUseCase mfaManagementUseCase,
            EmailService emailService,
            JwtTokenService jwtTokenService
    ) {
        return new UserManagementUseCaseImpl(
                repository,
                mfaManagementUseCase,
                emailService,
                jwtTokenService
        );
    }
}
