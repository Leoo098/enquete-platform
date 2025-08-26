package com.project.enquete.core.enquete_platform.security.services;

public interface TokenBlacklistService {
    void addToBlacklist(String token);
    boolean isBlacklisted(String token);
}
