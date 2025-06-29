package com.zerotrust.auth_gateway.infrastructure.repository.repositories.implementations;

import com.zerotrust.auth_gateway.application.repository.UserRepository;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.infrastructure.repository.entities.UserEntity;
import com.zerotrust.auth_gateway.infrastructure.repository.repositories.interfaces.JpaUserRepository;

import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {
    private JpaUserRepository jpaUserRepository;

    public UserRepositoryImpl(JpaUserRepository jpaUserRepository) {
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
