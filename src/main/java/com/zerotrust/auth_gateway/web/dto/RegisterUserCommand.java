package com.zerotrust.auth_gateway.web.dto;

public class RegisterUserCommand {
    private String username;
    private String rawPassword;

    public RegisterUserCommand(String username, String password) {
        this.username = username;
        this.rawPassword = password;
    }

    public String getRawPassword() {
        return rawPassword;
    }

    public void setRawPassword(String rawPassword) {
        this.rawPassword = rawPassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
