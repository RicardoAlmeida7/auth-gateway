package com.zerotrust.auth_gateway.application.port.out;

import com.zerotrust.auth_gateway.domain.model.User;

import java.util.Optional;

public interface UserRepositoryPort {
    void save(User user);
    Optional<User> findByUsername(String username);
}
