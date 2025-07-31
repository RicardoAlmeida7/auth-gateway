package com.zerotrust.auth_gateway.infrastructure.config.messaging;

import com.zerotrust.auth_gateway.domain.service.interfaces.EmailService;
import com.zerotrust.auth_gateway.infrastructure.email.MockEmailService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class EmailConfig {

    @Bean
    public EmailService emailService() {
        return new MockEmailService();
    }
}
