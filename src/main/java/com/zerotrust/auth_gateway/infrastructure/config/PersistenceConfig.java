package com.zerotrust.auth_gateway.infrastructure.config;

import com.zerotrust.auth_gateway.application.port.out.UserRepositoryPort;
import com.zerotrust.auth_gateway.infrastructure.persistence.repositories.implementations.UserRepositoryAdapter;
import com.zerotrust.auth_gateway.infrastructure.persistence.repositories.interfaces.JpaUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersistenceConfig {

    @Bean
    public UserRepositoryPort userRepositoryPort(JpaUserRepository jpaUserRepository) {
        return new UserRepositoryAdapter(jpaUserRepository);
    }
}
