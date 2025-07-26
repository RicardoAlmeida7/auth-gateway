package com.zerotrust.auth_gateway.application.usecase.interfaces;

import com.zerotrust.auth_gateway.domain.model.User;

public interface MfaManagementUseCase {
    void enableMfa(String username);
    void disableMfa(String username);
    String prepareMfaIfEnabled(User user);
}
