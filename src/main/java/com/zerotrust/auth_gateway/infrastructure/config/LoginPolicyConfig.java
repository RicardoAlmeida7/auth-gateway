package com.zerotrust.auth_gateway.infrastructure.config;

import com.zerotrust.auth_gateway.application.usecase.implementations.auth.LoginPolicyUseCaseImpl;
import com.zerotrust.auth_gateway.application.usecase.interfaces.auth.LoginPolicyUseCase;
import com.zerotrust.auth_gateway.domain.repository.LoginPolicyRepository;
import com.zerotrust.auth_gateway.infrastructure.repository.repositories.implementations.LoginPolicyRepositoryImpl;
import com.zerotrust.auth_gateway.infrastructure.repository.repositories.interfaces.JpaLoginPolicyRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoginPolicyConfig {

    @Bean
    public LoginPolicyRepository loginPolicyRepository(JpaLoginPolicyRepository jpaLoginPolicyRepository) {
        return new LoginPolicyRepositoryImpl(jpaLoginPolicyRepository);
    }

    @Bean
    public LoginPolicyUseCase loginPolicyUseCase(LoginPolicyRepository loginPolicyRepository) {
        return new LoginPolicyUseCaseImpl(loginPolicyRepository);
    }
}
