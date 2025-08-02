package com.zerotrust.auth_gateway.infrastructure.repository.repositories.interfaces;

import com.zerotrust.auth_gateway.infrastructure.repository.entities.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE UserEntity u SET u.blocked = true WHERE u.id = :userId")
    int blockUserById(@Param("userId") UUID userId);

    @Transactional
    @Modifying
    @Query("UPDATE UserEntity u SET u.blocked = false WHERE u.id = :userId")
    int unblockUserById(@Param("userId") UUID userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM UserEntity u WHERE u.id = :userId")
    int deleteUserById(@Param("userId") UUID userId);
}
