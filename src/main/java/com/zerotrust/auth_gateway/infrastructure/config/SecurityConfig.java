package com.zerotrust.auth_gateway.infrastructure.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.zerotrust.auth_gateway.application.service.implementations.LoginAttemptServiceImpl;
import com.zerotrust.auth_gateway.application.service.interfaces.JwtTokenService;
import com.zerotrust.auth_gateway.application.service.interfaces.LoginAttemptService;
import com.zerotrust.auth_gateway.application.usecase.implementations.auth.MfaManagementUseCaseImpl;
import com.zerotrust.auth_gateway.application.usecase.implementations.auth.AuthenticationUseCaseImpl;
import com.zerotrust.auth_gateway.application.usecase.interfaces.auth.MfaManagementUseCase;
import com.zerotrust.auth_gateway.application.usecase.interfaces.auth.AuthenticationUseCase;
import com.zerotrust.auth_gateway.domain.repository.LoginPolicyRepository;
import com.zerotrust.auth_gateway.domain.repository.UserRepository;
import com.zerotrust.auth_gateway.domain.service.EmailService;
import com.zerotrust.auth_gateway.domain.service.TOTPService;
import com.zerotrust.auth_gateway.infrastructure.security.filter.JwtAuthenticationFilter;
import com.zerotrust.auth_gateway.infrastructure.security.jwt.JwtTokenGenerator;
import com.zerotrust.auth_gateway.infrastructure.security.providers.CustomAuthenticationProvider;
import com.zerotrust.auth_gateway.infrastructure.security.totp.TOTPServiceImpl;
import com.zerotrust.auth_gateway.infrastructure.security.userdetails.UserDetailsServiceAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity,
            AuthenticationProvider authenticationProvider,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers ->
                        headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/v1/auth/login").permitAll()
                                .requestMatchers("/api/v1/auth/refresh-token").permitAll()
                                .requestMatchers("/api/v1/user/request-password-reset").permitAll()
                                .requestMatchers("/api/v1/user/reset-password").permitAll()
                                .requestMatchers("/api/v1/user/reset-password").permitAll()
                                .requestMatchers("/api/v1/user/activate").permitAll()
                                .requestMatchers("/api/v1/user/register").permitAll()
                                .requestMatchers("/api/v1/user/resend-activation-email").permitAll()
                                .requestMatchers("/h2-console/**").permitAll()
                                .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsServiceAdapter userDetailsServiceAdapter, PasswordEncoder passwordEncoder) {
        return new CustomAuthenticationProvider(userDetailsServiceAdapter, passwordEncoder);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenGenerator jwtTokenGenerator) {
        return new JwtAuthenticationFilter(jwtTokenGenerator);
    }

    @Bean
    public JWTVerifier jwtVerifier(Algorithm algorithm) {
        return JWT.require(algorithm).build();
    }

    @Bean
    public AuthenticationUseCase userLoginUseCase(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            TOTPService totpService,
            LoginAttemptService loginAttemptService,
            JwtTokenService jwtTokenService
    ) {
        return new AuthenticationUseCaseImpl(authenticationManager, userRepository, totpService, loginAttemptService, jwtTokenService);
    }

    @Bean
    public TOTPService totpService() {
        return new TOTPServiceImpl();
    }

    @Bean
    public LoginAttemptService loginAttemptService(UserRepository userRepository, LoginPolicyRepository loginPolicyRepository) {
        return new LoginAttemptServiceImpl(userRepository, loginPolicyRepository);
    }

    @Bean
    public MfaManagementUseCase mfaManagementUseCase(UserRepository userRepository, TOTPService totpService, EmailService emailService) {
        return new MfaManagementUseCaseImpl(userRepository, totpService, emailService);
    }
}
