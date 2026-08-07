package com.finnex.finance_app.common.ratelimit.Service;


import com.finnex.finance_app.common.exceptions.BadRequestException;
import com.finnex.finance_app.common.exceptions.RateLimitExceedException;
import com.finnex.finance_app.common.ratelimit.util.RateLimitKeyGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimitServiceImpl implements RateLimitService {

    private final RedisService redisService;
    private final RateLimitKeyGenerator rateLimitKeyGenerator;

    @Override
    public void validateRequest(
            HttpServletRequest request,
            String bucketName,
            int maxRequests,
            Duration window
    ) {

        String key = rateLimitKeyGenerator.generate(request, bucketName);

        long requests = redisService.increment(key, window);

        if (requests > maxRequests) {
            throw new RateLimitExceedException("Rate limit exceeded! You have reached the maximum number of requests.");
        }
    }


}