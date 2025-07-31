package com.zerotrust.auth_gateway.infrastructure.config.core;

import com.zerotrust.auth_gateway.application.service.interfaces.JwtTokenService;
import com.zerotrust.auth_gateway.application.usecase.implementations.activation.AccountActivationUseCaseImpl;
import com.zerotrust.auth_gateway.application.usecase.implementations.auth.PasswordResetUseCaseImpl;
import com.zerotrust.auth_gateway.application.usecase.implementations.admin.AdminUserManagementUseCaseImpl;
import com.zerotrust.auth_gateway.application.usecase.implementations.registration.PublicRegistrationUseCaseImpl;
import com.zerotrust.auth_gateway.application.usecase.implementations.user.UserProfileUseCaseImpl;
import com.zerotrust.auth_gateway.application.usecase.interfaces.activation.AccountActivationUseCase;
import com.zerotrust.auth_gateway.application.usecase.interfaces.admin.AdminUserManagementUseCase;
import com.zerotrust.auth_gateway.application.usecase.interfaces.auth.MfaManagementUseCase;
import com.zerotrust.auth_gateway.application.usecase.interfaces.auth.PasswordResetUseCase;
import com.zerotrust.auth_gateway.application.usecase.interfaces.registration.PublicRegistrationUseCase;
import com.zerotrust.auth_gateway.application.usecase.interfaces.user.UserProfileUseCase;
import com.zerotrust.auth_gateway.domain.enums.Role;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.domain.service.implementation.UserUpdateValidatorImpl;
import com.zerotrust.auth_gateway.domain.service.interfaces.EmailService;
import com.zerotrust.auth_gateway.domain.service.interfaces.UserUpdateValidator;
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
    public PublicRegistrationUseCase registerUserUseCase(
            PasswordEncoder passwordEncoder,
            UserRepository useRepositoryPort,
            MfaManagementUseCase mfaManagementUseCase,
            EmailService emailService,
            JwtTokenService jwtTokenService
    ) {

        return new PublicRegistrationUseCaseImpl(
                passwordEncoder,
                useRepositoryPort,
                mfaManagementUseCase,
                emailService,
                jwtTokenService
        );
    }

    @Bean
    public AccountActivationUseCase activateAccountUseCase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService
    ) {
        return new AccountActivationUseCaseImpl(userRepository, passwordEncoder, jwtTokenService);
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
    public AdminUserManagementUseCase userManagementUseCase(
            UserRepository repository,
            MfaManagementUseCase mfaManagementUseCase,
            EmailService emailService,
            JwtTokenService jwtTokenService,
            UserUpdateValidator userUpdateValidator
    ) {
        return new AdminUserManagementUseCaseImpl(
                repository,
                mfaManagementUseCase,
                emailService,
                jwtTokenService,
                userUpdateValidator
        );
    }

    @Bean
    public UserProfileUseCase userProfileUseCase(UserRepository userRepository, UserUpdateValidator userUpdateValidator) {
        return new UserProfileUseCaseImpl(userRepository, userUpdateValidator);
    }

    @Bean
    public UserUpdateValidator userUpdateValidator(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return new UserUpdateValidatorImpl(userRepository, passwordEncoder);
    }
}
