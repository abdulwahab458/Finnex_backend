package com.finnex.finance_app.common.ratelimit.Service;

import java.time.Duration;
import java.util.Optional;

public interface RedisService {

    void set(String key, String value);

    void set(String key, String value, Duration ttl);

    Optional<String> get(String key);

    boolean exists(String key);

    void delete(String key);

    long increment(String key);

    long increment(String key, Duration ttl);
}
