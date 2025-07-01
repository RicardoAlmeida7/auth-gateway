package com.zerotrust.auth_gateway.application.usecase.interfaces;

public interface AuthServiceUseCase {
    String login(String username, String password);
}
