package com.zerotrust.auth_gateway.application.service.implementations;

import com.zerotrust.auth_gateway.application.service.interfaces.LoginAttemptService;
import com.zerotrust.auth_gateway.domain.exception.AuthenticationFailedException;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;

public class LoginAttemptServiceImpl implements LoginAttemptService {

    private final UserRepository userRepository;

    private static final int MAX_ATTEMPTS = 3;
    private static final long LOCK_TIME_MILLIS = 60_000;

    public LoginAttemptServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void recordFailure(User user) {
        int newAttempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(newAttempts);

        if (newAttempts >= MAX_ATTEMPTS) {
            user.setLastFailedLoginTime(System.currentTimeMillis());
        }

        userRepository.save(user);
    }

    @Override
    public void reset(User user) {
        user.setFailedLoginAttempts(0);
        user.setLastFailedLoginTime(0L);
        userRepository.save(user);
    }

    @Override
    public void checkLock(User user) {
        long now = System.currentTimeMillis();
        if (user.getFailedLoginAttempts() >= MAX_ATTEMPTS) {
            if ((now - user.getLastFailedLoginTime()) < LOCK_TIME_MILLIS) {
                throw new AuthenticationFailedException("Account is temporarily locked. Try again later.");
            }
            recordFailure(user);
        }
    }
}
