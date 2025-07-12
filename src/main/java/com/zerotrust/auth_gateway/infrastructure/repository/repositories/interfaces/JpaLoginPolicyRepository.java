package com.zerotrust.auth_gateway.infrastructure.repository.repositories.interfaces;

import com.zerotrust.auth_gateway.infrastructure.repository.entities.LoginPolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaLoginPolicyRepository extends JpaRepository<LoginPolicyEntity, UUID> {
}
