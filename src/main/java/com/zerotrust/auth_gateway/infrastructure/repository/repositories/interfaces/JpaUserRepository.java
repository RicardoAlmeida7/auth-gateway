package com.zerotrust.auth_gateway.infrastructure.repository.repositories.interfaces;

import com.zerotrust.auth_gateway.infrastructure.repository.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String username);
}
