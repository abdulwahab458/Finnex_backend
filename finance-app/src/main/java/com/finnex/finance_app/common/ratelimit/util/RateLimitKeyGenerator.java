package com.finnex.finance_app.common.ratelimit.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class RateLimitKeyGenerator {

    /**
     * Generates a unique Redis key for rate limiting.
     *
     * Example:
     * login:192.168.1.10
     * register:192.168.1.10
     * chat:192.168.1.10
     */
    public String generate(
            HttpServletRequest request,
            String bucketName
    ) {

        String ipAddress = extractClientIp(request);

        return String.format("%s:%s", bucketName, ipAddress);
    }

    /**
     * Supports proxies/load balancers.
     */
    private String extractClientIp(HttpServletRequest request) {

        String forwardedFor = request.getHeader("X-Forwarded-For");

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        String realIp = request.getHeader("X-Real-IP");

        if (realIp != null && !realIp.isBlank()) {
            return realIp;
        }

        return request.getRemoteAddr();
    }
}