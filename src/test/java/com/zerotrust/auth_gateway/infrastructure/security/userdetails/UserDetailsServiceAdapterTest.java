package com.zerotrust.auth_gateway.infrastructure.security.userdetails;

import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        UUID id = UUID.randomUUID();
        List<String> roles = List.of("ROLE_USER", "ROLE_ADMIN");
        User user = new User(id, "testuser", "hash", "testuser@example.com",false, "secret", true, roles, true);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userDetailsServiceAdapter.loadUserByUsername("testuser");

        // Assert
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("hash", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertEquals(roles.size(), userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
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
