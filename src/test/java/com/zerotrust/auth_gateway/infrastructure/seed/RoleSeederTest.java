package com.zerotrust.auth_gateway.infrastructure.seed;

import com.zerotrust.auth_gateway.domain.enums.Role;
import com.zerotrust.auth_gateway.infrastructure.repository.entities.RoleEntity;
import com.zerotrust.auth_gateway.infrastructure.repository.repositories.interfaces.JpaRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class RoleSeederTest {

    private JpaRoleRepository jpaRoleRepository;
    private RoleSeeder roleSeeder;

    @BeforeEach
    void setUp() {
        jpaRoleRepository = mock(JpaRoleRepository.class);
        roleSeeder = new RoleSeeder(jpaRoleRepository);
    }

    @Test
    void shouldInsertMissingRoles() {
        // Arrange
        when(jpaRoleRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(jpaRoleRepository.save(any(RoleEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        roleSeeder.run();

        // Assert
        for (Role role : Role.values()) {
            verify(jpaRoleRepository).findByName(role.name());
            verify(jpaRoleRepository).save(argThat(entity -> role.name().equals(entity.getName())));
        }
    }

    @Test
    void shouldNotInsertExistingRoles() {
        // Arrange
        for (Role role : Role.values()) {
            when(jpaRoleRepository.findByName(role.name())).thenReturn(
                    Optional.of(new RoleEntity(UUID.randomUUID(), role.name()))
            );
        }

        // Act
        roleSeeder.run();

        // Assert
        for (Role role : Role.values()) {
            verify(jpaRoleRepository).findByName(role.name());
        }

        verify(jpaRoleRepository, never()).save(any());
    }
}
