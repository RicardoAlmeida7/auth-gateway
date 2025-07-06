package com.zerotrust.auth_gateway.web.controller;

import com.zerotrust.auth_gateway.application.usecase.interfaces.ActivateAccountUseCase;
import com.zerotrust.auth_gateway.application.usecase.interfaces.UserServiceUseCase;
import com.zerotrust.auth_gateway.web.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    private UserServiceUseCase userServiceUseCase;
    private ActivateAccountUseCase activateAccountUseCase;
    private UserController userController;

    @BeforeEach
    void setUp() {
        userServiceUseCase = mock(UserServiceUseCase.class);
        activateAccountUseCase = mock(ActivateAccountUseCase.class);
        userController = new UserController(userServiceUseCase, activateAccountUseCase);
    }

    @Test
    void shouldRegisterUserAndReturnCreatedStatus() {
        RegisterRequest request = new RegisterRequest("username", "Password1@", "user@example.com", List.of("ROLE_USER"), false);

        ResponseEntity<Void> response = userController.register(request);

        verify(userServiceUseCase, times(1)).register(request);
        assertEquals(201, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void shouldActivateAccountAndReturnOkStatus() {
        String token = "some-valid-token";

        ResponseEntity<Void> response = userController.activateAccount(token);

        verify(activateAccountUseCase, times(1)).activate(token);
        assertEquals(200, response.getStatusCodeValue());
        assertNull(response.getBody());
    }
}
