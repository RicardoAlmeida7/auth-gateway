package com.zerotrust.auth_gateway.infrastructure.security.userdetails;

import com.zerotrust.auth_gateway.application.repository.UserRepository;
import com.zerotrust.auth_gateway.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserDetailsServiceAdapterTest {

    private UserRepository userRepository;
    private UserDetailsServiceAdapter userDetailsServiceAdapter;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userDetailsServiceAdapter = new UserDetailsServiceAdapter(userRepository);
    }

    @Test
    void shouldReturnUserDetailsWhenUserExists() {
        // Arrange
        User user = new User(UUID.randomUUID(), "testuser", "hash", false, "secret", true);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act
        UserDetails result = userDetailsServiceAdapter.loadUserByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsServiceAdapter.loadUserByUsername("unknown")
        );

        assertEquals("User not found.", exception.getMessage());
    }
}
