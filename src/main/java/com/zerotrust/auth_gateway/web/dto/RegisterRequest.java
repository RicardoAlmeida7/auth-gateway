package com.zerotrust.auth_gateway.web.dto;

import com.zerotrust.auth_gateway.domain.enums.Role;

import java.util.List;
import java.util.Objects;

public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private List<String> roles;
    private boolean mfaEnabled;

    public RegisterRequest() {
    }

    public RegisterRequest(String username, String password, String email, List<String> roles, boolean mfaEnabled) {
        this.mfaEnabled = mfaEnabled;
        if (roles == null || roles.isEmpty()) {
            roles = List.of(Role.ROLE_USER.name());
        }
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = roles;
    }

    public RegisterRequest(String username, String password, String email, List<String> roles) {
        this(username, password, email, roles, false);
    }

    public RegisterRequest(String username, String password, String email, boolean mfaEnabled) {
        this(username, password, email, List.of(Role.ROLE_USER.name()), mfaEnabled);
    }

    public RegisterRequest(String username, String password, String email) {
        this(username, password, email, List.of(Role.ROLE_USER.name()), false);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RegisterRequest that)) return false;
        return Objects.equals(username, that.username) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isMfaEnabled() {
        return mfaEnabled;
    }

    public void setMfaEnabled(boolean mfaEnabled) {
        this.mfaEnabled = mfaEnabled;
    }
}
