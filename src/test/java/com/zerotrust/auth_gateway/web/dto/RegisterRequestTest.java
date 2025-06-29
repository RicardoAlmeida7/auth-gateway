package com.zerotrust.auth_gateway.web.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterRequestTest {

    @Test
    void shouldCreateRegisterRequestWithFields() {
        String username = "newUser";
        String password = "newPass";

        RegisterRequest request = new RegisterRequest(username, password);

        assertEquals(username, request.getUsername());
        assertEquals(password, request.getPassword());
    }

    @Test
    void shouldAllowFieldMutationViaSetters() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user2");
        request.setPassword("pass2");

        assertEquals("user2", request.getUsername());
        assertEquals("pass2", request.getPassword());
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
