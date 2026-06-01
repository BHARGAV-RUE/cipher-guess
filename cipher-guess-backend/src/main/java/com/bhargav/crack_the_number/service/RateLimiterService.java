package com.bhargav.crack_the_number.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    // Separate buckets per IP per action
    private final Map<String, Bucket> registerBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> loginBuckets    = new ConcurrentHashMap<>();
    private final Map<String, Bucket> gameBuckets     = new ConcurrentHashMap<>();

    // 5 registrations per IP per hour
    private Bucket newRegisterBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(5, Refill.intervally(5, Duration.ofHours(1))))
                .build();
    }

    // 20 login attempts per IP per 15 minutes
    private Bucket newLoginBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(20, Refill.intervally(20, Duration.ofMinutes(15))))
                .build();
    }

    // 60 game actions per IP per minute
    private Bucket newGameBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(60, Refill.intervally(60, Duration.ofMinutes(1))))
                .build();
    }

    public boolean allowRegister(String ip) {
        return registerBuckets.computeIfAbsent(ip, k -> newRegisterBucket()).tryConsume(1);
    }

    public boolean allowLogin(String ip) {
        return loginBuckets.computeIfAbsent(ip, k -> newLoginBucket()).tryConsume(1);
    }

    public boolean allowGame(String ip) {
        return gameBuckets.computeIfAbsent(ip, k -> newGameBucket()).tryConsume(1);
    }
}