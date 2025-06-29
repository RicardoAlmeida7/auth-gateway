package com.zerotrust.auth_gateway.infrastructure.security.userdetails;

import com.zerotrust.auth_gateway.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

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
                true,
                List.of("ROLE_USER", "ROLE_ADMIN")  // importante setar roles para testar authorities
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
    void shouldReturnCorrectAuthorities() {
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        assertEquals(2, roles.size());
        assertTrue(roles.contains("ROLE_USER"));
        assertTrue(roles.contains("ROLE_ADMIN"));
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
