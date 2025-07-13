package com.zerotrust.auth_gateway.application.usecase.interfaces;

public interface MfaManagementUseCase {
    void enableMfa(String username);
    void disableMfa(String username);
}
