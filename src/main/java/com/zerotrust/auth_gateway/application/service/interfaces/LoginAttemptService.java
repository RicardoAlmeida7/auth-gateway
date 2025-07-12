package com.zerotrust.auth_gateway.application.service.interfaces;

import com.zerotrust.auth_gateway.domain.model.User;

public interface LoginAttemptService {
    void recordFailure(User user);
    void reset(User user);
    void checkLock(User user);
}
