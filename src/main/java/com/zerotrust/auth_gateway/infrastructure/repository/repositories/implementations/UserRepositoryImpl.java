package com.zerotrust.auth_gateway.infrastructure.repository.repositories.implementations;

import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.infrastructure.repository.entities.RoleEntity;
import com.zerotrust.auth_gateway.infrastructure.repository.entities.UserEntity;
import com.zerotrust.auth_gateway.infrastructure.repository.repositories.interfaces.JpaRoleRepository;
import com.zerotrust.auth_gateway.infrastructure.repository.repositories.interfaces.JpaUserRepository;

import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {
    private final JpaUserRepository jpaUserRepository;
    private final JpaRoleRepository jpaRoleRepository;

    public UserRepositoryImpl(JpaUserRepository jpaUserRepository, JpaRoleRepository jpaRoleRepository) {
        this.jpaUserRepository = jpaUserRepository;
        this.jpaRoleRepository = jpaRoleRepository;
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

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email).map(this::mapToDomain);
    }

    private UserEntity mapToEntity(User user) {
        List<RoleEntity> roleEntities = user.getRoles().stream()
                .map(roleName -> jpaRoleRepository.findByName(roleName)
                        .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName)))
                .toList();
        UserEntity entity = new UserEntity(
                user.getId(),
                user.getUsername(),
                user.getPasswordHash(),
                user.getEmail(),
                user.isMfaEnabled(),
                user.getMfaSecret(),
                user.isFirstAccessRequired(),
                user.isEnabled(),
                roleEntities
        );
        entity.setFailedLoginAttempts(user.getFailedLoginAttempts());
        entity.setLastFailedLoginTime(user.getLastFailedLoginTime());
        return entity;
    }

    private User mapToDomain(UserEntity userEntity) {
        List<String> roleNames = userEntity.getRoles().stream()
                .map(RoleEntity::getName)
                .toList();

        User user = new User(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getPasswordHash(),
                userEntity.getEmail(),
                userEntity.isMfaEnabled(),
                userEntity.getMfaSecret(),
                userEntity.isEnabled(),
                roleNames,
                userEntity.isFirstAccessRequired()
        );
        user.setFailedLoginAttempts(userEntity.getFailedLoginAttempts());
        user.setLastFailedLoginTime(userEntity.getLastFailedLoginTime());
        return user;
    }
}
