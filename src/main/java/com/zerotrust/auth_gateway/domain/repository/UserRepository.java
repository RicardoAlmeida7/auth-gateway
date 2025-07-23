package com.zerotrust.auth_gateway.domain.repository;

import com.zerotrust.auth_gateway.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    void save(User user);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID id);
    void delete(User user);
}
