package com.zerotrust.auth_gateway.application.usecase.implementations.auth;

import com.zerotrust.auth_gateway.application.dto.request.auth.AuthenticationRequest;
import com.zerotrust.auth_gateway.application.dto.response.auth.JwtResponse;
import com.zerotrust.auth_gateway.application.dto.response.auth.UserLoginInfoResponse;
import com.zerotrust.auth_gateway.application.service.interfaces.JwtTokenService;
import com.zerotrust.auth_gateway.application.service.interfaces.LoginAttemptService;
import com.zerotrust.auth_gateway.application.usecase.interfaces.auth.AuthenticationUseCase;
import com.zerotrust.auth_gateway.domain.exception.AuthenticationFailedException;
import com.zerotrust.auth_gateway.domain.exception.FirstAccessPasswordRequiredException;
import com.zerotrust.auth_gateway.domain.exception.UserBlockedException;
import com.zerotrust.auth_gateway.domain.exception.UserNotFoundException;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.domain.service.interfaces.TOTPService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthenticationUseCaseImpl implements AuthenticationUseCase {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TOTPService totpService;
    private final LoginAttemptService loginAttemptService;
    private final JwtTokenService jwtTokenService;

    public AuthenticationUseCaseImpl(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            TOTPService totpService,
            LoginAttemptService loginAttemptService, JwtTokenService jwtTokenService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.totpService = totpService;
        this.loginAttemptService = loginAttemptService;
        this.jwtTokenService = jwtTokenService;
    }

    public JwtResponse login(AuthenticationRequest request) {
        if (request == null)
            throw new AuthenticationFailedException("Authentication request must include a username or email and a password.");

        User user = userRepository
                .findByUsername(request.userId())
                .or(() -> userRepository.findByEmail(request.userId()))
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.isBlocked()) throw new UserBlockedException("User account is blocked.");

        validateFirstAccess(user);
        loginAttemptService.checkLock(user);
        validateMfa(user, request);

        return authenticateAndGenerateToken(user, request);
    }

    @Override
    public JwtResponse refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new AuthenticationFailedException("Refresh token must be provided");
        }

        UUID userId;
        try {
            String userIdStr = jwtTokenService.validateRefreshToken(refreshToken);
            userId = UUID.fromString(userIdStr);
        } catch (Exception ex) {
            throw new UserNotFoundException("User not found.");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found."));

        if (user.isBlocked()) throw new UserBlockedException("User account is blocked.");

        return jwtTokenService.generateAuthToken(user);
    }

    @Override
    public UserLoginInfoResponse getUserStatusInfo(String userId) {
        User user = userRepository.findByUsername(userId)
                .or(() -> userRepository.findByEmail(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return new UserLoginInfoResponse(userId, user.isMfaEnabled(), user.isEnabled());
    }

    @Override
    public void logout(String accessTokenHeader, String refreshToken) {
        String accessToken = accessTokenHeader.replace("Bearer ", "");

        jwtTokenService.validateAuthToken(accessToken);
        jwtTokenService.validateRefreshToken(refreshToken);

        jwtTokenService.blacklistAuthTokens(accessToken, refreshToken);
    }

    private void validateFirstAccess(User user) {
        if (!user.isEnabled())
            throw new AuthenticationFailedException("User account is not activated.");
        if (user.isFirstAccessRequired())
            throw new FirstAccessPasswordRequiredException("You must reset your password before logging in for the first time.");
    }

    private void validateMfa(User user, AuthenticationRequest request) {
        if (user.isMfaEnabled()) {
            if (request.otp() == null || request.otp().isBlank()) {
                throw new AuthenticationFailedException("OTP is required for MFA-enabled accounts.");
            }
            boolean validOtp = totpService.verifyCode(user.getMfaSecret(), request.otp());

            if (!validOtp) {
                loginAttemptService.recordFailure(user);
                throw new AuthenticationFailedException("Invalid OTP code.");
            }
        }
    }

    private JwtResponse authenticateAndGenerateToken(User user, AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), request.password())
            );

            loginAttemptService.reset(user);
            return jwtTokenService.generateAuthToken(user);
        } catch (Exception exception) {
            loginAttemptService.recordFailure(user);
            throw new AuthenticationFailedException("Invalid password.");
        }
    }
}
