package com.zerotrust.auth_gateway.domain.model;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class User {

    public User(UUID id, String username, String passwordHash, String email, boolean mfaEnabled, String mfaSecret, boolean enabled, List<String> roles, boolean firstAccessRequired) {
        this.firstAccessRequired = firstAccessRequired;
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null.");
        }
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.mfaEnabled = mfaEnabled;
        this.mfaSecret = mfaSecret;
        this.enabled = enabled;
        this.roles = roles != null ? roles : Collections.emptyList();
    }

    private UUID id;
    private String username;
    private String passwordHash;
    private boolean mfaEnabled;
    private String mfaSecret;
    private boolean enabled;
    private List<String> roles;
    private String email;
    private boolean firstAccessRequired;
    private int failedLoginAttempts = 0;
    private long lastFailedLoginTime = 0;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isMfaEnabled() {
        return mfaEnabled;
    }

    public void setMfaEnabled(boolean mfaEnabled) {
        this.mfaEnabled = mfaEnabled;
    }

    public String getMfaSecret() {
        return mfaSecret;
    }

    public void setMfaSecret(String mfaSecret) {
        this.mfaSecret = mfaSecret;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles != null ? roles : Collections.emptyList();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", roles=" + roles +
                '}';
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isFirstAccessRequired() {
        return firstAccessRequired;
    }

    public void setFirstAccessRequired(boolean firstAccessRequired) {
        this.firstAccessRequired = firstAccessRequired;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public long getLastFailedLoginTime() {
        return lastFailedLoginTime;
    }

    public void setLastFailedLoginTime(long lastFailedLoginTime) {
        this.lastFailedLoginTime = lastFailedLoginTime;
    }
}
