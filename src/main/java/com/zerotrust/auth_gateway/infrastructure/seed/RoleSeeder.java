package com.zerotrust.auth_gateway.infrastructure.seed;

import com.zerotrust.auth_gateway.domain.enums.Role;
import com.zerotrust.auth_gateway.infrastructure.repository.entities.RoleEntity;
import com.zerotrust.auth_gateway.infrastructure.repository.repositories.interfaces.JpaRoleRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class RoleSeeder {

    private final JpaRoleRepository jpaRoleRepository;

    public RoleSeeder(JpaRoleRepository jpaRoleRepository) {
        this.jpaRoleRepository = jpaRoleRepository;
    }

    @Transactional
    public void run() {
        for (Role role : Role.values()) {

            boolean exists = jpaRoleRepository.findByName(role.name()).isPresent();
            if (!exists) {
                jpaRoleRepository.save(new RoleEntity(null, role.name()));
            }
        }
    }
}
