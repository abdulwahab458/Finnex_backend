package com.finnex.finance_app.common.ratelimit.Service;
import jakarta.servlet.http.HttpServletRequest;

import java.time.Duration;

public interface RateLimitService {

    void validateRequest(
            HttpServletRequest request,
            String bucketName,
            int maxRequests,
            Duration window
    );

}