package com.zerotrust.auth_gateway.infrastructure.repository.repositories.implementations;

import com.zerotrust.auth_gateway.domain.model.LoginPolicy;
import com.zerotrust.auth_gateway.domain.repository.LoginPolicyRepository;
import com.zerotrust.auth_gateway.infrastructure.repository.entities.LoginPolicyEntity;
import com.zerotrust.auth_gateway.infrastructure.repository.repositories.interfaces.JpaLoginPolicyRepository;

import java.util.UUID;

public class LoginPolicyRepositoryImpl implements LoginPolicyRepository {

    private static final UUID DEFAULT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final JpaLoginPolicyRepository jpaLoginPolicyRepository;

    public LoginPolicyRepositoryImpl(JpaLoginPolicyRepository jpaLoginPolicyRepository) {
        this.jpaLoginPolicyRepository = jpaLoginPolicyRepository;
    }

    @Override
    public LoginPolicy get() {
        LoginPolicyEntity entity = jpaLoginPolicyRepository.findById(DEFAULT_ID).orElseThrow();
        return new LoginPolicy(entity.getId(), entity.getMaxAttempts(), entity.getLockTimeMillis());
    }

    @Override
    public void update(LoginPolicy policy) {
        jpaLoginPolicyRepository.save(new LoginPolicyEntity(policy.getId(), policy.getMaxAttempts(), policy.getLockTimeMillis()));
    }
}
