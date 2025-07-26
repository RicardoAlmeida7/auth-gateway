package com.zerotrust.auth_gateway.application.usecase.implementations;

import com.zerotrust.auth_gateway.application.usecase.interfaces.MfaManagementUseCase;
import com.zerotrust.auth_gateway.domain.exception.UserNotFoundException;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.domain.service.EmailService;
import com.zerotrust.auth_gateway.domain.service.TOTPService;

public class MfaManagementUseCaseImpl implements MfaManagementUseCase {

    private final UserRepository userRepository;
    private final TOTPService totpService;
    private final EmailService emailService;

    public MfaManagementUseCaseImpl(UserRepository userRepository, TOTPService totpService, EmailService emailService) {
        this.userRepository = userRepository;
        this.totpService = totpService;
        this.emailService = emailService;
    }

    @Override
    public void enableMfa(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.isMfaEnabled()) throw new IllegalArgumentException("MFA already enabled.");

        String secret = totpService.generateSecret();
        String qrCodeUrl = totpService.generateQrCodeUrl(username, secret);

        user.setMfaSecret(secret);
        user.setMfaEnabled(true);
        userRepository.save(user);

        emailService.sendMfaSetupEmail(user.getEmail(), qrCodeUrl);
    }

    @Override
    public void disableMfa(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found."));

        if (!user.isMfaEnabled()) throw new IllegalArgumentException("MFA already disabled.");

        user.setMfaEnabled(false);
        user.setMfaSecret("");
        userRepository.save(user);
    }

    @Override
    public String prepareMfaIfEnabled(User user) {
        if (!user.isMfaEnabled()) return null;

        String secret = totpService.generateSecret();
        String qrCodeUrl = totpService.generateQrCodeUrl(user.getUsername(), secret);
        user.setMfaSecret(secret);
        return qrCodeUrl;
    }
}
