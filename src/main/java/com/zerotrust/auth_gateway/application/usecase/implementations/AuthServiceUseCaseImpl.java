package com.zerotrust.auth_gateway.application.usecase.implementations;

import com.zerotrust.auth_gateway.application.usecase.interfaces.AuthServiceUseCase;
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

    public AuthServiceUseCaseImpl(AuthenticationManager authenticationManager, JwtTokenGenerator jwtTokenGenerator, UserRepository userRepository, TOTPService totpService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.userRepository = userRepository;
        this.totpService = totpService;
    }

    public String login(String username, String password, String otp) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank.");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank.");
        }

        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.isEnabled()) {
            throw new IllegalArgumentException("User account is not activated.");
        }

        if (user.isMfaEnabled()) {
            if (otp == null || otp.isBlank()) {
                throw new IllegalArgumentException("OTP is required for MFA-enabled accounts.");
            }

            boolean validOtp = totpService.verifyCode(user.getMfaSecret(), otp);

            if (!validOtp) {
                // TODO implements errors count to block user or token
                throw  new IllegalArgumentException("Invalid OTP code.");
            }
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return jwtTokenGenerator.generateToken(username, roles);

        //TODO Refactor to support multi-factor and different authentication flows.
    }
}
