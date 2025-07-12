package com.zerotrust.auth_gateway.application.usecase.implementations;

import com.zerotrust.auth_gateway.application.dto.request.AuthenticationRequest;
import com.zerotrust.auth_gateway.application.service.interfaces.LoginAttemptService;
import com.zerotrust.auth_gateway.application.usecase.interfaces.AuthServiceUseCase;
import com.zerotrust.auth_gateway.domain.exception.AuthenticationFailedException;
import com.zerotrust.auth_gateway.domain.exception.FirstAccessPasswordRequiredException;
import com.zerotrust.auth_gateway.domain.model.User;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.domain.service.TOTPService;
import com.zerotrust.auth_gateway.infrastructure.security.jwt.JwtTokenGenerator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthServiceUseCaseImpl implements AuthServiceUseCase {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final UserRepository userRepository;
    private final TOTPService totpService;
    private final LoginAttemptService loginAttemptService;

    public AuthServiceUseCaseImpl(
            AuthenticationManager authenticationManager,
            JwtTokenGenerator jwtTokenGenerator,
            UserRepository userRepository,
            TOTPService totpService,
            LoginAttemptService loginAttemptService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.userRepository = userRepository;
        this.totpService = totpService;
        this.loginAttemptService = loginAttemptService;
    }

    public String login(AuthenticationRequest request) {
        if (request == null)
            throw new AuthenticationFailedException("Authentication request must include a username or email and a password.");

        User user = userRepository
                .findByUsername(request.username())
                .or(() -> userRepository.findByEmail(request.email()))
                .orElseThrow(() -> new AuthenticationFailedException("User not found"));

        validateFirstAccess(user);
        loginAttemptService.checkLock(user);
        validateMfa(user, request);

        return authenticateAndGenerateToken(user, request);
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

    private String authenticateAndGenerateToken(User user, AuthenticationRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );

            loginAttemptService.reset(user);
            return jwtTokenGenerator.generateToken(request.username(), getAuthorities(authentication));
        } catch (Exception exception) {
            loginAttemptService.recordFailure(user);
            throw new AuthenticationFailedException("Invalid password.");
        }
    }

    private List<String> getAuthorities(Authentication authentication) {
        return authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }
}
