package com.zerotrust.auth_gateway.infrastructure.security.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.zerotrust.auth_gateway.application.service.interfaces.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtTokenService jwtTokenService;
    private JwtAuthenticationFilter filter;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        jwtTokenService = mock(JwtTokenService.class);
        filter = new JwtAuthenticationFilter(jwtTokenService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);

        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_validToken_setsAuthentication() throws Exception {
        String token = "valid.token.value";
        String authHeader = "Bearer " + token;
        UUID userId = UUID.randomUUID();

        DecodedJWT decodedJWT = mock(DecodedJWT.class);
        Claim rolesClaim = mock(Claim.class);

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtTokenService.validateAuthToken(token)).thenReturn(decodedJWT);
        when(decodedJWT.getSubject()).thenReturn(userId.toString());
        when(decodedJWT.getClaim("roles")).thenReturn(rolesClaim);
        when(decodedJWT.getClaim("username")).thenReturn(rolesClaim);
        when(rolesClaim.asList(String.class)).thenReturn(List.of("ROLE_USER", "ROLE_ADMIN"));

        filter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));

        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void doFilterInternal_missingAuthorizationHeader_doesNotSetAuthentication() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void doFilterInternal_invalidToken_returnsUnauthorized() throws Exception {
        String token = "invalid.token";
        String authHeader = "Bearer " + token;

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtTokenService.validateAuthToken(token)).thenThrow(new JWTVerificationException("Invalid token"));

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        verify(filterChain, never()).doFilter(request, response);

        printWriter.flush();
        String responseContent = stringWriter.toString();
        assertTrue(responseContent.contains("UNAUTHORIZED"));
    }

}
