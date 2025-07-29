package com.zerotrust.auth_gateway.infrastructure.config;

import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.infrastructure.config.persistence.PersistenceConfig;
import com.zerotrust.auth_gateway.infrastructure.repository.repositories.implementations.UserRepositoryImpl;
import com.zerotrust.auth_gateway.infrastructure.repository.repositories.interfaces.JpaRoleRepository;
import com.zerotrust.auth_gateway.infrastructure.repository.repositories.interfaces.JpaUserRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class PersistenceConfigTest {

    @Test
    void userRepository_shouldReturnUserRepositoryImpl() {
        JpaUserRepository jpaUserRepository = mock(JpaUserRepository.class);
        JpaRoleRepository jpaRoleRepository = mock(JpaRoleRepository.class);
        PersistenceConfig config = new PersistenceConfig();

        UserRepository userRepository = config.userRepository(jpaUserRepository, jpaRoleRepository);

        assertNotNull(userRepository);
        assertInstanceOf(UserRepositoryImpl.class, userRepository);
    }
}
