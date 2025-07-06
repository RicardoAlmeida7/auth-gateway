package com.zerotrust.auth_gateway.domain.service;

public interface EmailService {
    void sendActivationEmail(String to, String activationToken, String activationLink, String qrCodeUrl);
}
