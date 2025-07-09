package com.zerotrust.auth_gateway.infrastructure.web.dto;

public class PasswordResetRequest {

    private String newPassword;
    private String confirmPassword;

    public PasswordResetRequest() {
    }

    public PasswordResetRequest(String newPassword, String confirmPassword) {
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
