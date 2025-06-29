package com.zerotrust.auth_gateway.infrastructure.security.userdetails;

import com.zerotrust.auth_gateway.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserDetailsAdapterTest {

    private User user;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        user = new User(
                UUID.randomUUID(),
                "testuser",
                "hashedpassword",
                false,
                "secret",
                true
        );
        userDetails = new UserDetailsAdapter(user);
    }

    @Test
    void shouldReturnUsername() {
        assertEquals("testuser", userDetails.getUsername());
    }

    @Test
    void shouldReturnPassword() {
        assertEquals("hashedpassword", userDetails.getPassword());
    }

    @Test
    void shouldReturnEmptyAuthorities() {
        assertTrue(userDetails.getAuthorities().isEmpty());
    }

    @Test
    void shouldReturnTrueForEnabled() {
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void shouldReturnTrueForAccountStatusMethods() {
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
    }

    @Test
    void shouldReturnOriginalDomainUser() {
        assertEquals(user, ((UserDetailsAdapter) userDetails).getDomainUser());
    }
}
