package com.zerotrust.auth_gateway.infrastructure.repository.repositories.interfaces;

import com.zerotrust.auth_gateway.infrastructure.repository.entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaRoleRepository extends JpaRepository<RoleEntity, UUID> {
    Optional<RoleEntity> findByName(String name);
}
