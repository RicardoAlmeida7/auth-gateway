package com.zerotrust.auth_gateway.infrastructure.security.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.zerotrust.auth_gateway.application.service.interfaces.JwtTokenService;
import com.zerotrust.auth_gateway.domain.exception.AuthenticationFailedException;
import com.zerotrust.auth_gateway.domain.exception.InvalidTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenService jwtTokenService;

    public JwtAuthenticationFilter(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                DecodedJWT decodedJWT = jwtTokenService.validateAuthToken(token);

                String username = decodedJWT.getSubject();
                List<SimpleGrantedAuthority> authorities = decodedJWT.getClaim("roles").asList(String.class)
                        .stream().map(SimpleGrantedAuthority::new).toList();

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            } catch (AuthenticationFailedException | InvalidTokenException | JWTVerificationException ex) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("""
                {
                  "timestamp": "%s",
                  "status": 401,
                  "error": "UNAUTHORIZED",
                  "message": "Token revoked."
                }
                """.formatted(java.time.Instant.now()));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
