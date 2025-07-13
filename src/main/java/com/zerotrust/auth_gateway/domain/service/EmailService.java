package com.zerotrust.auth_gateway.domain.service;

public interface EmailService {
    void sendActivationEmail(String to, String activationToken, String activationLink, String qrCodeUrl);
    void sendResetPasswordEmail(String to, String resetPasswordLink);
    void sendMfaSetupEmail(String to, String qrCodeUrl);
}
