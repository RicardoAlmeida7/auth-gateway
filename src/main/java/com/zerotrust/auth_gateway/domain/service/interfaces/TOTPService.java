package com.zerotrust.auth_gateway.domain.service.interfaces;

public interface TOTPService {

    String generateSecret();
    String generateCode(String secret);
    boolean verifyCode(String secret, String otp);
    String generateQrCodeUrl(String username, String secret);
}
