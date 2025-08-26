package com.project.enquete.core.enquete_platform.security.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CaffeineTokenBlacklistService implements TokenBlacklistService{

    private final Cache<String, Boolean> tokenBlacklist = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();

    @Override
    public void addToBlacklist(String token) {
        tokenBlacklist.put(token, true);
    }

    @Override
    public boolean isBlacklisted(String token) {
        return tokenBlacklist.getIfPresent(token) != null;
    }
}
