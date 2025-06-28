package com.zerotrust.auth_gateway.infrastructure.persistence.repositories.implementations;

import com.zerotrust.auth_gateway.application.port.out.UserRepositoryPort;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.infrastructure.persistence.entities.UserEntity;
import com.zerotrust.auth_gateway.infrastructure.persistence.repositories.interfaces.JpaUserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//@Repository
public class UserRepositoryAdapter implements UserRepositoryPort {
    private JpaUserRepository jpaUserRepository;

    public UserRepositoryAdapter(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public void save(User user) {
        UserEntity entity = mapToEntity(user);
        jpaUserRepository.save(entity);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaUserRepository.findByUsername(username).map(this::mapToDomain);
    }

    private UserEntity mapToEntity(User user) {

        return new UserEntity(
                user.getId(),
                user.getUsername(),
                user.getPasswordHash(),
                user.isMfaEnabled(),
                user.getMfaSecret(),
                user.isEnabled()
        );
    }

    private User mapToDomain(UserEntity userEntity) {
        return new User(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getPasswordHash(),
                userEntity.isMfaEnabled(),
                userEntity.getMfaSecret(),
                userEntity.isEnabled()
        );
    }
}
