package com.zerotrust.auth_gateway.application.service.implementations;

import com.zerotrust.auth_gateway.application.service.interfaces.LoginAttemptService;
import com.zerotrust.auth_gateway.domain.exception.AuthenticationFailedException;
import com.zerotrust.auth_gateway.domain.model.LoginPolicy;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.domain.repository.LoginPolicyRepository;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;

public class LoginAttemptServiceImpl implements LoginAttemptService {

    private final UserRepository userRepository;

    private final LoginPolicyRepository loginPolicyRepository;

    public LoginAttemptServiceImpl(UserRepository userRepository, LoginPolicyRepository loginPolicyRepository) {
        this.userRepository = userRepository;
        this.loginPolicyRepository = loginPolicyRepository;
    }

    @Override
    public void recordFailure(User user) {
        int newAttempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(newAttempts);

        if (newAttempts >= loginPolicyRepository.get().getMaxAttempts()) {
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
        LoginPolicy loginPolicy = loginPolicyRepository.get();
        if (user.getFailedLoginAttempts() >= loginPolicy.getMaxAttempts()) {
            if ((now - user.getLastFailedLoginTime()) < loginPolicy.getLockTimeMillis()) {
                throw new AuthenticationFailedException("Account is temporarily locked. Try again later.");
            }
            recordFailure(user);
        }
    }
}
