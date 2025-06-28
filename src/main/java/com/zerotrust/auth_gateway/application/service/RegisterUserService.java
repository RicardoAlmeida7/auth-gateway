package com.zerotrust.auth_gateway.application.service;

import com.zerotrust.auth_gateway.application.port.in.RegisterUserUseCase;
import com.zerotrust.auth_gateway.application.port.out.UserRepositoryPort;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.web.dto.RegisterUserCommand;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RegisterUserService implements RegisterUserUseCase {

    private final PasswordEncoder passwordEncoder;
    private final UserRepositoryPort useRepositoryPort;

    public RegisterUserService(PasswordEncoder passwordEncoder, UserRepositoryPort useRepositoryPort) {
        this.passwordEncoder = passwordEncoder;
        this.useRepositoryPort = useRepositoryPort;
    }

    @Override
    public void register(RegisterUserCommand command) {
        String passwordHashed = passwordEncoder.encode(command.getRawPassword());

        User user = new User(
                UUID.randomUUID(),
                command.getUsername(),
                passwordHashed,
                false,
                "",
                true
        );

        useRepositoryPort.save(user);
    }
}
