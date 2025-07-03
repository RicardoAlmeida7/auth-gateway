package com.zerotrust.auth_gateway.application.usecase.implementations;

import com.zerotrust.auth_gateway.application.usecase.interfaces.RegisterUserUseCase;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.domain.enums.Role;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.domain.validation.PasswordValidator;
import com.zerotrust.auth_gateway.web.dto.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository useRepositoryPort;

    public RegisterUserUseCaseImpl(PasswordEncoder passwordEncoder, UserRepository useRepositoryPort) {
        this.passwordEncoder = passwordEncoder;
        this.useRepositoryPort = useRepositoryPort;
    }

    @Override
    public void register(RegisterRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("RegisterUserCommand cannot be null.");
        }

        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank.");
        }

        PasswordValidator.validate(request.getPassword());

        String passwordHashed = passwordEncoder.encode(request.getPassword());
        List<String> roles = request.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = List.of(Role.ROLE_USER.name());
        }

        User user = new User(
                UUID.randomUUID(),
                request.getUsername(),
                passwordHashed,
                false,
                "",
                true,
                roles
        );

        useRepositoryPort.save(user);
        // TODO: Additional validations (e.g., email format, password strength) will be added as the project evolves.
    }
}
