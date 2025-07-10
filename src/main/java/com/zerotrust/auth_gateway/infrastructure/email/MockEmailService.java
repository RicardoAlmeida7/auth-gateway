package com.zerotrust.auth_gateway.infrastructure.email;

import com.zerotrust.auth_gateway.domain.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockEmailService implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(MockEmailService.class);
    @Override
    public void sendActivationEmail(String to, String activationToken, String activationLink, String qrCodeUrl) {
        logger.info("Sending activation email to: {}", to);
        logger.info("Activation token: {}", activationToken);
        logger.info("Activation link: {}", activationLink);
        if (qrCodeUrl != null && !qrCodeUrl.isBlank()) {
            logger.info("QR Code URL for MFA: {}", qrCodeUrl);
        }
    }

    @Override
    public void sendResetPasswordEmail(String to, String resetPasswordLink) {
        logger.info("Sending reset password email to: {}", to);
        logger.info("Reset password link: {}", resetPasswordLink);
    }
}
