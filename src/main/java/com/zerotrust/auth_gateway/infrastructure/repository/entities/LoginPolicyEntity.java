package com.zerotrust.auth_gateway.infrastructure.repository.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "login_policy")
public class LoginPolicyEntity {

    @Id
    private UUID id;

    @Column(nullable = false, name = "max_attempts")
    private int maxAttempts;

    @Column(nullable = false, name = "lock_time_millis")
    private long lockTimeMillis;

    public LoginPolicyEntity() {
    }

    public LoginPolicyEntity(UUID id, int maxAttempts, long lockTimeMillis) {
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
