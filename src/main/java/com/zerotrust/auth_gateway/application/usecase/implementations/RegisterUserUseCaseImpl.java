package com.zerotrust.auth_gateway.application.usecase.implementations;

import com.zerotrust.auth_gateway.application.usecase.interfaces.RegisterUserUseCase;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.domain.validation.EmailValidator;
import com.zerotrust.auth_gateway.domain.validation.PasswordValidator;
import com.zerotrust.auth_gateway.domain.validation.RoleValidator;
import com.zerotrust.auth_gateway.domain.validation.UsernameValidator;
import com.zerotrust.auth_gateway.web.dto.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

        UsernameValidator.validate(request.getUsername());
        PasswordValidator.validate(request.getPassword());
        EmailValidator.validate(request.getEmail());
        RoleValidator.validate(request.getRoles());

        String passwordHashed = passwordEncoder.encode(request.getPassword());
        User user = new User(
                UUID.randomUUID(),
                request.getUsername(),
                passwordHashed,
                request.getEmail(),
                false,
                "",
                true,
                request.getRoles()
        );

        useRepositoryPort.save(user);
    }
}
