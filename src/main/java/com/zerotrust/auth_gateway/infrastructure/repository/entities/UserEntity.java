package com.zerotrust.auth_gateway.infrastructure.repository.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 50, unique = true)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    private boolean mfaEnabled;

    @Column(length = 100)
    private String mfaSecret;

    private boolean firstAccessRequired;

    private boolean enabled;

    private int failedLoginAttempts;

    private long lastFailedLoginTime;

    public UserEntity() {
    }

    public UserEntity(
            UUID id,
            String username,
            String passwordHash,
            String email,
            boolean mfaEnabled,
            String mfaSecret, boolean firstAccessRequired,
            boolean enabled,
            List<RoleEntity> roles) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.mfaEnabled = mfaEnabled;
        this.mfaSecret = mfaSecret;
        this.firstAccessRequired = firstAccessRequired;
        this.enabled = enabled;
        this.roles = roles != null ? roles : new ArrayList<>();
    }

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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<RoleEntity> roles = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserEntity that)) return false;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", mfaEnabled=" + mfaEnabled +
                ", enabled=" + enabled +
                '}';
    }

    public List<RoleEntity> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleEntity> roles) {
        this.roles = roles != null ? roles : new ArrayList<>();
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

    public long getLastFailedLoginTime() {
        return lastFailedLoginTime;
    }

    public void setLastFailedLoginTime(long lastFailedLoginTime) {
        this.lastFailedLoginTime = lastFailedLoginTime;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }
}
