package com.zerotrust.auth_gateway.domain.repository;

import com.zerotrust.auth_gateway.domain.model.User;

import java.util.Optional;

public interface UserRepository {
    void save(User user);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
