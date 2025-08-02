package com.zerotrust.auth_gateway.domain.repository;

import com.zerotrust.auth_gateway.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    void save(User user);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID id);
    void delete(UUID userId);
    List<User> getAll();
    void blockUser(UUID userId);
    void unblockUser(UUID userId);
}
