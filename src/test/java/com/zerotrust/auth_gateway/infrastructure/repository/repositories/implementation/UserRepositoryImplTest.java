package com.zerotrust.auth_gateway.infrastructure.repository.repositories.implementation;

import com.zerotrust.auth_gateway.application.repository.UserRepository;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.infrastructure.repository.entities.UserEntity;
import com.zerotrust.auth_gateway.infrastructure.repository.repositories.implementations.UserRepositoryImpl;
import com.zerotrust.auth_gateway.infrastructure.repository.repositories.interfaces.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserRepositoryImplTest {

    private JpaUserRepository jpaUserRepository;
    private UserRepository userRepositoryAdapter;

    @BeforeEach
    void setup() {
        jpaUserRepository = mock(JpaUserRepository.class);
        userRepositoryAdapter = new UserRepositoryImpl(jpaUserRepository);
    }

    @Test
    void save_shouldCallJpaSaveWithMappedEntity() {
        User user = new User(UUID.randomUUID(), "testuser", "hashedpass", false, "", true);

        userRepositoryAdapter.save(user);

        ArgumentCaptor<UserEntity> entityCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(jpaUserRepository).save(entityCaptor.capture());

        UserEntity savedEntity = entityCaptor.getValue();
        assertEquals(user.getId(), savedEntity.getId());
        assertEquals(user.getUsername(), savedEntity.getUsername());
        assertEquals(user.getPasswordHash(), savedEntity.getPasswordHash());
        assertEquals(user.isMfaEnabled(), savedEntity.isMfaEnabled());
        assertEquals(user.getMfaSecret(), savedEntity.getMfaSecret());
        assertEquals(user.isEnabled(), savedEntity.isEnabled());
    }

    @Test
    void findByUsername_shouldReturnMappedUser_whenUserExists() {
        UUID id = UUID.randomUUID();
        UserEntity userEntity = new UserEntity(id, "testuser", "hashedpass", false, "", true);

        when(jpaUserRepository.findByUsername("testuser")).thenReturn(Optional.of(userEntity));

        Optional<User> userOptional = userRepositoryAdapter.findByUsername("testuser");

        assertTrue(userOptional.isPresent());

        User user = userOptional.get();
        assertEquals(id, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("hashedpass", user.getPasswordHash());
        assertFalse(user.isMfaEnabled());
        assertEquals("", user.getMfaSecret());
        assertTrue(user.isEnabled());
    }

    @Test
    void findByUsername_shouldReturnEmpty_whenUserNotFound() {
        when(jpaUserRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        Optional<User> userOptional = userRepositoryAdapter.findByUsername("unknown");

        assertFalse(userOptional.isPresent());
    }
}
