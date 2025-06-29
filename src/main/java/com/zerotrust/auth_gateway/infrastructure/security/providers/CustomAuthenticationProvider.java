package com.zerotrust.auth_gateway.infrastructure.security.providers;

import com.zerotrust.auth_gateway.infrastructure.security.userdetails.UserDetailsServiceAdapter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CustomAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsServiceAdapter userDetailsServiceAdapter;
    private PasswordEncoder passwordEncoder;

    public CustomAuthenticationProvider() {
    }

    public CustomAuthenticationProvider(UserDetailsServiceAdapter userDetailsServiceAdapter, PasswordEncoder passwordEncoder) {
        this.userDetailsServiceAdapter = userDetailsServiceAdapter;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String rawPassword = authentication.getCredentials().toString();

        if (username == null || username.isBlank() || rawPassword == null || rawPassword.isBlank()) {
            throw new BadCredentialsException("Username or password cannot be blank");
        }

        UserDetails userDetails = userDetailsServiceAdapter.loadUserByUsername(username);

        if (!passwordEncoder.matches(rawPassword, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        return new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(),
                null,
                userDetails.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
