package com.zerotrust.auth_gateway.infrastructure.config.security;

import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.BucketProxy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.function.Supplier;

@Component
@Aspect
public class RatelimiterAspect {

    private final ProxyManager<String> proxyManager;
    private final Supplier<BucketConfiguration> bucketConfiguration;

    public RatelimiterAspect(
            ProxyManager<String> proxyManager,
            Supplier<BucketConfiguration> bucketConfiguration
    ) {
        this.proxyManager = proxyManager;
        this.bucketConfiguration = bucketConfiguration;
    }

    @Around("@annotation(Ratelimited)")
    public Object ratelimit(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes()).getRequest();

        String clientIp = getClientIp(request);

        BucketProxy bucket = proxyManager.builder()
                .build(clientIp, bucketConfiguration);

        if (bucket.tryConsume(1)) {
            return joinPoint.proceed();
        } else {
            HttpServletResponse response = ((ServletRequestAttributes)
                    RequestContextHolder.getRequestAttributes()).getResponse();
            if (response != null) {
                sendRateLimitExceeded(response);
            }
            return null;
        }
    }

    private void sendRateLimitExceeded(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");
        response.getWriter().write("""
                {
                  "timestamp": "%s",
                  "status": 429,
                  "error": "Too many requests",
                  "message": "You have exceeded the allowed number of requests. Please try again later."
                }
                """.formatted(java.time.Instant.now()));
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
