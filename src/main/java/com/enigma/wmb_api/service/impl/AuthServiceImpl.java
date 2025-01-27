package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.dto.request.AuthRequest;
import com.enigma.wmb_api.dto.response.AuthResponse;
import com.enigma.wmb_api.entity.UserAccount;
import com.enigma.wmb_api.service.AuthService;
import com.enigma.wmb_api.service.JwtService;
import com.enigma.wmb_api.service.RefreshTokenService;
import com.enigma.wmb_api.service.UserService;
import com.enigma.wmb_api.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final ValidationUtil validationUtil;

    @Override
    public AuthResponse login(AuthRequest request) {
        validationUtil.validate(request);
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserAccount userAccount = (UserAccount) authentication.getPrincipal();
        String refreshToken = refreshTokenService.createToken(userAccount.getId());
        String accessToken = jwtService.generateAccessToken(userAccount);
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(userAccount.getRole().getDescription())
                .build();
    }

    @Override
    public AuthResponse refreshToken(String token) {
        String userId = refreshTokenService.getUserIdByToken(token);
        UserAccount userAccount = userService.getById(userId);
        String newRefreshToken = refreshTokenService.rotateRefreshToken(userId);
        String newToken = jwtService.generateAccessToken(userAccount);
        return AuthResponse.builder()
                .accessToken(newToken)
                .refreshToken(newRefreshToken)
                .role(userAccount.getRole().getDescription())
                .build();
    }

    @Override
    public void logout(String accessToken) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccount userAccount = (UserAccount) authentication.getPrincipal();
        refreshTokenService.deleteRefreshToken(userAccount.getId());
        jwtService.blacklistAccessToken(accessToken);
    }

    @Override
    public int generateTokenExpireInSecond() {
        return refreshTokenService.getRefreshTokenExpiryInHour() * 60 * 60;
    }


}
