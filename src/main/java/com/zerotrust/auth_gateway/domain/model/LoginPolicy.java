package com.zerotrust.auth_gateway.domain.model;

import java.util.UUID;

public class LoginPolicy {

    private final UUID id;
    private int maxAttempts;
    private long lockTimeMillis;

    public LoginPolicy(UUID id, int maxAttempts, long lockTimeMillis) {
        this.id = id;
        this.maxAttempts = maxAttempts;
        this.lockTimeMillis = lockTimeMillis;
    }

    public UUID getId() {
        return id;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public long getLockTimeMillis() {
        return lockTimeMillis;
    }

    public void setLockTimeMillis(long lockTimeMillis) {
        this.lockTimeMillis = lockTimeMillis;
    }
}
