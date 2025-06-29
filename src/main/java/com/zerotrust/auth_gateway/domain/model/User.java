package com.zerotrust.auth_gateway.domain.model;

import java.util.UUID;

public class User {

    public User(UUID id, String username, String passwordHash, boolean mfaEnabled, String mfaSecret, boolean enabled) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null.");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank.");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be null or blank.");
        }
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.mfaEnabled = mfaEnabled;
        this.mfaSecret = mfaSecret;
        this.enabled = enabled;
    }

    private UUID id;
    private String username;
    private String passwordHash;
    private boolean mfaEnabled;
    private String mfaSecret;
    private boolean enabled;

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
}
