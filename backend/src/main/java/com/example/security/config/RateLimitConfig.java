package com.example.security.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitConfig {
    @Value("${app.rate-limit.login-capacity}") private int loginCapacity;
    @Value("${app.rate-limit.login-refill-tokens}") private int loginRefillTokens;
    @Value("${app.rate-limit.login-refill-seconds}") private int loginRefillSeconds;
    @Value("${app.rate-limit.refresh-capacity}") private int refreshCapacity;
    @Value("${app.rate-limit.refresh-refill-tokens}") private int refreshRefillTokens;
    @Value("${app.rate-limit.refresh-refill-seconds}") private int refreshRefillSeconds;

    // NOTE: These in-memory maps grow with unique IPs. For production use, apply
    // an eviction strategy (e.g., Caffeine cache with TTL). Acceptable for a lab.
    private final Map<String, Bucket> loginBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> refreshBuckets = new ConcurrentHashMap<>();

    public Bucket resolveLoginBucket(String ip) {
        return loginBuckets.computeIfAbsent(ip, k ->
                Bucket.builder()
                        .addLimit(Bandwidth.builder()
                                .capacity(loginCapacity)
                                .refillGreedy(loginRefillTokens, Duration.ofSeconds(loginRefillSeconds))
                                .build())
                        .build());
    }

    public Bucket resolveRefreshBucket(String ip) {
        return refreshBuckets.computeIfAbsent(ip, k ->
                Bucket.builder()
                        .addLimit(Bandwidth.builder()
                                .capacity(refreshCapacity)
                                .refillGreedy(refreshRefillTokens, Duration.ofSeconds(refreshRefillSeconds))
                                .build())
                        .build());
    }
}
