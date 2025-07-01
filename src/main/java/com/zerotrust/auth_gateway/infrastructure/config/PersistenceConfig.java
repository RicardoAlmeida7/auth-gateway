package com.zerotrust.auth_gateway.infrastructure.config;

import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.infrastructure.repository.repositories.implementations.UserRepositoryImpl;
import com.zerotrust.auth_gateway.infrastructure.repository.repositories.interfaces.JpaRoleRepository;
import com.zerotrust.auth_gateway.infrastructure.repository.repositories.interfaces.JpaUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersistenceConfig {

    @Bean
    public UserRepository userRepository(JpaUserRepository jpaUserRepository, JpaRoleRepository jpaRoleRepository) {
        return new UserRepositoryImpl(jpaUserRepository, jpaRoleRepository);
    }
}
