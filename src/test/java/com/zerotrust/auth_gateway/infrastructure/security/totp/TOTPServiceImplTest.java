package com.zerotrust.auth_gateway.infrastructure.security.totp;

import com.zerotrust.auth_gateway.domain.service.interfaces.TOTPService;
import org.apache.commons.codec.binary.Base32;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TOTPServiceImplTest {

    private TOTPService totpService;

    @BeforeEach
    void setUp() {
        totpService = new TOTPServiceImpl();
    }

    @Test
    void generateSecret_shouldReturnNonEmptyBase32String() {
        String secret = totpService.generateSecret();
        assertNotNull(secret);
        assertFalse(secret.isBlank());

        // Base32 validation: should decode without error
        byte[] decoded = new Base32().decode(secret);
        assertNotNull(decoded);
        assertTrue(decoded.length > 0);
    }

    @Test
    void generateCode_shouldReturnSixDigitCode() {
        String secret = totpService.generateSecret();

        String code = totpService.generateCode(secret);
        assertNotNull(code);
        assertEquals(6, code.length());
        assertTrue(code.matches("\\d{6}")); // Only digits
    }

    @Test
    void verifyCode_shouldReturnTrueForValidCode() {
        String secret = totpService.generateSecret();
        String code = totpService.generateCode(secret);

        boolean valid = totpService.verifyCode(secret, code);
        assertTrue(valid);
    }

    @Test
    void verifyCode_shouldReturnFalseForInvalidCode() {
        String secret = totpService.generateSecret();

        boolean valid = totpService.verifyCode(secret, "000000");
        assertFalse(valid);
    }

    @Test
    void generateQrCodeUrl_shouldReturnValidOtpAuthUrl() {
        String username = "testuser@example.com";
        String secret = totpService.generateSecret();

        String url = totpService.generateQrCodeUrl(username, secret);
        assertNotNull(url);
        assertTrue(url.startsWith("otpauth://totp/"));
        assertTrue(url.contains("secret=" + secret));
        assertTrue(url.contains("issuer=ZeroTrustAuthGateway"));
        assertTrue(url.contains("digits=6"));
        assertTrue(url.contains("algorithm=SHA1"));
        assertTrue(url.contains("period=30"));
    }
}
