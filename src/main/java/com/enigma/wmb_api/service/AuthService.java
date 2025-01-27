package com.enigma.wmb_api.service;

import com.enigma.wmb_api.dto.request.AuthRequest;
import com.enigma.wmb_api.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(AuthRequest request);
    AuthResponse refreshToken(String token);
    void logout(String accessToken);
    int generateTokenExpireInSecond();
}
