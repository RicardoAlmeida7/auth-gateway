package com.zerotrust.auth_gateway.infrastructure.config;

import com.zerotrust.auth_gateway.application.usecase.implementations.ActivateAccountUseCaseImpl;
import com.zerotrust.auth_gateway.application.usecase.implementations.PasswordResetUseCaseImpl;
import com.zerotrust.auth_gateway.application.usecase.implementations.UserManagementUseCaseImpl;
import com.zerotrust.auth_gateway.application.usecase.implementations.UserRegistrationUseImpl;
import com.zerotrust.auth_gateway.application.usecase.interfaces.ActivateAccountUseCase;
import com.zerotrust.auth_gateway.application.usecase.interfaces.PasswordResetUseCase;
import com.zerotrust.auth_gateway.application.usecase.interfaces.UserManagementUseCase;
import com.zerotrust.auth_gateway.application.usecase.interfaces.UserRegistrationUse;
import com.zerotrust.auth_gateway.domain.enums.Role;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.domain.service.EmailService;
import com.zerotrust.auth_gateway.domain.service.TOTPService;
import com.zerotrust.auth_gateway.infrastructure.security.jwt.JwtTokenGenerator;
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
    public UserRegistrationUse registerUserUseCase(
            PasswordEncoder passwordEncoder,
            UserRepository useRepositoryPort,
            TOTPService totpService,
            JwtTokenGenerator jwtTokenGenerator,
            EmailService emailService) {

        return new UserRegistrationUseImpl(
                passwordEncoder,
                useRepositoryPort,
                totpService,
                jwtTokenGenerator,
                emailService);
    }

    @Bean
    public ActivateAccountUseCase activateAccountUseCase(JwtTokenGenerator jwtTokenGenerator, UserRepository userRepository) {
        return new ActivateAccountUseCaseImpl(jwtTokenGenerator, userRepository);
    }

    @Bean
    public PasswordResetUseCase passwordResetUseCase(
            UserRepository userRepository,
            EmailService emailService,
            JwtTokenGenerator jwtTokenGenerator,
            PasswordEncoder passwordEncoder
    ) {
        return new PasswordResetUseCaseImpl(userRepository, emailService, jwtTokenGenerator, passwordEncoder);
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
    public UserManagementUseCase userManagementUseCase(UserRepository repository) {
        return new UserManagementUseCaseImpl(repository);
    }
}
