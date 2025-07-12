package com.zerotrust.auth_gateway.infrastructure.seed;

import com.zerotrust.auth_gateway.infrastructure.repository.entities.LoginPolicyEntity;
import com.zerotrust.auth_gateway.infrastructure.repository.repositories.interfaces.JpaLoginPolicyRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class LoginPolicySeeder {

    private static final UUID DEFAULT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final JpaLoginPolicyRepository jpaLoginPolicyRepository;

    public LoginPolicySeeder(JpaLoginPolicyRepository jpaLoginPolicyRepository) {
        this.jpaLoginPolicyRepository = jpaLoginPolicyRepository;
    }

    @Transactional
    public void run() {
        Optional<LoginPolicyEntity> optionalEntity = jpaLoginPolicyRepository.findById(DEFAULT_ID);

        if (optionalEntity.isEmpty()) {
            jpaLoginPolicyRepository.save(new LoginPolicyEntity(DEFAULT_ID, 3, 60000));
        }
    }
}
