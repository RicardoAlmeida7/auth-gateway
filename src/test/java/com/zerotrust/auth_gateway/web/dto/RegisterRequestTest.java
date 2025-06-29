package com.zerotrust.auth_gateway.web.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterRequestTest {

    @Test
    void shouldCreateRegisterRequestWithFields() {
        String username = "newUser";
        String password = "newPass";

        RegisterRequest request = new RegisterRequest(username, password);

        assertEquals(username, request.getUsername());
        assertEquals(password, request.getPassword());
        assertNull(request.getRoles(), "Roles should be null if not set");
    }

    @Test
    void shouldCreateRegisterRequestWithRoles() {
        String username = "userWithRoles";
        String password = "passWithRoles";
        List<String> roles = List.of("ROLE_USER", "ROLE_ADMIN");

        RegisterRequest request = new RegisterRequest(username, password, roles);

        assertEquals(username, request.getUsername());
        assertEquals(password, request.getPassword());
        assertEquals(roles, request.getRoles());
    }

    @Test
    void shouldAllowFieldMutationViaSetters() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user2");
        request.setPassword("pass2");
        request.setRoles(List.of("ROLE_USER"));

        assertEquals("user2", request.getUsername());
        assertEquals("pass2", request.getPassword());
        assertEquals(List.of("ROLE_USER"), request.getRoles());
    }

    @Test
    void shouldImplementEqualsAndHashCodeCorrectly() {
        RegisterRequest r1 = new RegisterRequest("abc", "123");
        RegisterRequest r2 = new RegisterRequest("abc", "123");
        RegisterRequest r3 = new RegisterRequest("def", "456");

        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void shouldReturnMeaningfulToString() {
        RegisterRequest request = new RegisterRequest("userX", "passY");
        String str = request.toString();

        assertTrue(str.contains("userX"));
        assertTrue(str.contains("passY"));
    }
}
