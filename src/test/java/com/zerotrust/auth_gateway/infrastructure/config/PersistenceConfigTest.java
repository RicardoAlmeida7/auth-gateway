package com.zerotrust.auth_gateway.infrastructure.config;

import com.zerotrust.auth_gateway.application.repository.UserRepository;
import com.zerotrust.auth_gateway.infrastructure.repository.repositories.implementations.UserRepositoryImpl;
import com.zerotrust.auth_gateway.infrastructure.repository.repositories.interfaces.JpaUserRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class PersistenceConfigTest {

    @Test
    void userRepositoryPort_shouldReturnUserRepositoryAdapter() {
        // Arrange
        JpaUserRepository jpaUserRepository = mock(JpaUserRepository.class);
        PersistenceConfig config = new PersistenceConfig();

        // Act
        UserRepository userRepository = config.userRepository(jpaUserRepository);

        // Assert
        assertNotNull(userRepository);
        assertInstanceOf(UserRepositoryImpl.class, userRepository);
    }
}
