package com.zerotrust.auth_gateway.application.service;

import com.zerotrust.auth_gateway.application.usecase.RegisterUserUseCase;
import com.zerotrust.auth_gateway.application.repository.UserRepository;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.web.dto.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RegisterUserService implements RegisterUserUseCase {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository useRepositoryPort;

    public RegisterUserService(PasswordEncoder passwordEncoder, UserRepository useRepositoryPort) {
        this.passwordEncoder = passwordEncoder;
        this.useRepositoryPort = useRepositoryPort;
    }

    @Override
    public void register(RegisterRequest command) {
        if (command == null) {
            throw new IllegalArgumentException("RegisterUserCommand cannot be null.");
        }

        if (command.getUsername() == null || command.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank.");
        }

        if (command.getPassword() == null || command.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank.");
        }
        String passwordHashed = passwordEncoder.encode(command.getPassword());

        User user = new User(
                UUID.randomUUID(),
                command.getUsername(),
                passwordHashed,
                false,
                "",
                true
        );

        useRepositoryPort.save(user);
        // TODO: Additional validations (e.g., email format, password strength) will be added as the project evolves.
    }
}
