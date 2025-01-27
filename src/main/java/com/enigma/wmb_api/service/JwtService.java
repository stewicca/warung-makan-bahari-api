package com.enigma.wmb_api.service;

import com.enigma.wmb_api.entity.UserAccount;
import jakarta.servlet.http.HttpServletRequest;

public interface JwtService {
    String generateAccessToken(UserAccount userAccount);
    String getUserId(String token);
    String extractTokenFromRequest(HttpServletRequest request);
    void blacklistAccessToken(String bearerToken);
    boolean isTokenBlacklisted(String token);
}
