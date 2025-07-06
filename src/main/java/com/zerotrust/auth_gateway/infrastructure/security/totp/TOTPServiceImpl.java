package com.zerotrust.auth_gateway.infrastructure.security.totp;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import com.zerotrust.auth_gateway.domain.service.TOTPService;
import org.apache.commons.codec.binary.Base32;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;

public class TOTPServiceImpl implements TOTPService {

    private final TimeBasedOneTimePasswordGenerator passwordGenerator;
    private static final String ISSUER = "ZeroTrustAuthGateway";

    public TOTPServiceImpl() {
        int TIME_STEP = 30;
        this.passwordGenerator = new TimeBasedOneTimePasswordGenerator(Duration.ofSeconds(TIME_STEP));
    }

    @Override
    public String generateSecret() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(passwordGenerator.getAlgorithm());
            int SHA1_KEY_SIZE = 160;
            keyGenerator.init(SHA1_KEY_SIZE);
            SecretKey key = keyGenerator.generateKey();
            return new Base32().encodeToString(key.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unable to generate secret key", e);
        }
    }

    @Override
    public String generateCode(String secret) {
        try {
            byte[] secretBytes = new Base32().decode(secret);
            SecretKey key = new javax.crypto.spec.SecretKeySpec(secretBytes, passwordGenerator.getAlgorithm());
            return String.format("%06d", passwordGenerator.generateOneTimePassword(key, Instant.now()));
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Invalid secret key", e);
        }
    }

    @Override
    public boolean verifyCode(String secret, String otp) {
        try {
            byte[] secretBytes = new Base32().decode(secret);
            SecretKey key = new SecretKeySpec(secretBytes, passwordGenerator.getAlgorithm());
            Instant now = Instant.now();

            for (int offset = -1; offset <= 1; offset++) {
                Instant time = now.plusSeconds(offset * passwordGenerator.getTimeStep().getSeconds());
                String candidate = String.format("%06d", passwordGenerator.generateOneTimePassword(key, time));

                if (candidate.equals(otp)) {
                    return true;
                }
            }

            return false;
        } catch (InvalidKeyException | IllegalArgumentException e) {
            throw new RuntimeException("Invalid key for OTP verification", e);
        }
    }

    @Override
    public String generateQrCodeUrl(String username, String secret) {
        String encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8);
        String encodedIssuer = URLEncoder.encode(ISSUER, StandardCharsets.UTF_8);

        return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s&algorithm=SHA1&digits=6&period=30",
                encodedIssuer,
                encodedUsername,
                secret,
                encodedIssuer);
    }
}
