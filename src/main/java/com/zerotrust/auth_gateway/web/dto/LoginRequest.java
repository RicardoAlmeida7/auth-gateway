package com.zerotrust.auth_gateway.web.dto;

import java.util.Objects;

public class LoginRequest {

    public LoginRequest() {
    }

    private String username;
    private String password;
    private String otp;

    public LoginRequest(String username, String password, String otp) {
        this.username = username;
        this.password = password;
        this.otp = otp;
    }

    public LoginRequest(String username, String password) {
        this(username, password, null);
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
        if (!(o instanceof LoginRequest that)) return false;
        return Objects.equals(username, that.username) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
