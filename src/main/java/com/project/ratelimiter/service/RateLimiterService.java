package com.project.ratelimiter.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class RateLimiterService {

    private final StringRedisTemplate redisTemplate;
    private final RedisScript<Long> tokenBucketScript;

    @Value("${rate-limiter.capacity}")
    private long capacity;

    @Value("${rate-limiter.refill-rate}")
    private long refillRate;

    public RateLimiterService(StringRedisTemplate redisTemplate, RedisScript<Long> tokenBucketScript) {
        this.redisTemplate = redisTemplate;
        this.tokenBucketScript = tokenBucketScript;
    }

    public boolean isAllowed(String clientId) {
        String tokenKey = "rate_limiter:" + clientId + ":tokens";
        String timestampKey = "rate_limiter:" + clientId + ":timestamp";

        long now = Instant.now().getEpochSecond();

        Long result = redisTemplate.execute(
                tokenBucketScript,
                List.of(tokenKey, timestampKey),
                String.valueOf(capacity),
                String.valueOf(refillRate),
                String.valueOf(now),
                "1" // Request 1 token
        );

        return result != null && result == 1L;
    }
}
