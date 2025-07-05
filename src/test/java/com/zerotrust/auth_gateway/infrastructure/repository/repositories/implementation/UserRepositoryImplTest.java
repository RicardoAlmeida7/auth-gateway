package com.zerotrust.auth_gateway.infrastructure.repository.repositories.implementation;

import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.infrastructure.repository.entities.RoleEntity;
import com.zerotrust.auth_gateway.infrastructure.repository.entities.UserEntity;
import com.zerotrust.auth_gateway.infrastructure.repository.repositories.implementations.UserRepositoryImpl;
import com.zerotrust.auth_gateway.infrastructure.repository.repositories.interfaces.JpaRoleRepository;
import com.zerotrust.auth_gateway.infrastructure.repository.repositories.interfaces.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserRepositoryImplTest {

    private JpaUserRepository jpaUserRepository;
    private JpaRoleRepository jpaRoleRepository;
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        jpaUserRepository = mock(JpaUserRepository.class);
        jpaRoleRepository = mock(JpaRoleRepository.class);
        userRepository = new UserRepositoryImpl(jpaUserRepository, jpaRoleRepository);
    }

    @Test
    void save_shouldCallJpaSaveWithMappedEntity() {
        UUID userId = UUID.randomUUID();
        String roleName = "ROLE_USER";
        RoleEntity roleEntity = new RoleEntity(UUID.randomUUID(), roleName);
        User user = new User(userId, "testuser", "hashedpass", "testuser@example.com", false, "", true, List.of(roleName));

        // Mock para simular busca da role no banco
        when(jpaRoleRepository.findByName(roleName)).thenReturn(Optional.of(roleEntity));

        userRepository.save(user);

        ArgumentCaptor<UserEntity> entityCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(jpaUserRepository).save(entityCaptor.capture());

        UserEntity savedEntity = entityCaptor.getValue();
        assertEquals(userId, savedEntity.getId());
        assertEquals("testuser", savedEntity.getUsername());
        assertEquals("hashedpass", savedEntity.getPasswordHash());
        assertFalse(savedEntity.isMfaEnabled());
        assertEquals("", savedEntity.getMfaSecret());
        assertTrue(savedEntity.isEnabled());

        // Verifica roles
        assertNotNull(savedEntity.getRoles());
        assertEquals(1, savedEntity.getRoles().size());
        assertEquals(roleName, savedEntity.getRoles().get(0).getName());
    }

    @Test
    void findByUsername_shouldReturnMappedUser_whenUserExists() {
        UUID id = UUID.randomUUID();
        RoleEntity role = new RoleEntity(UUID.randomUUID(), "ROLE_USER");
        UserEntity userEntity = new UserEntity(id, "testuser", "hashedpass", "testuser@example.com", false, "", true, List.of(role));

        when(jpaUserRepository.findByUsername("testuser")).thenReturn(Optional.of(userEntity));

        Optional<User> userOptional = userRepository.findByUsername("testuser");

        assertTrue(userOptional.isPresent());

        User user = userOptional.get();
        assertEquals(id, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("hashedpass", user.getPasswordHash());
        assertFalse(user.isMfaEnabled());
        assertEquals("", user.getMfaSecret());
        assertTrue(user.isEnabled());
        assertEquals(List.of("ROLE_USER"), user.getRoles());
    }

    @Test
    void findByUsername_shouldReturnEmpty_whenUserNotFound() {
        when(jpaUserRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        Optional<User> userOptional = userRepository.findByUsername("unknown");

        assertFalse(userOptional.isPresent());
    }

    @Test
    void save_shouldThrowException_whenRoleNotFound() {
        UUID userId = UUID.randomUUID();
        String missingRole = "ROLE_MISSING";
        User user = new User(userId, "testuser", "hashedpass", "testuser@example.com", false, "", true, List.of(missingRole));

        when(jpaRoleRepository.findByName(missingRole)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userRepository.save(user);
        });
        assertEquals("Role not found: " + missingRole, exception.getMessage());
    }
}
