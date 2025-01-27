package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.constant.Constant;
import com.enigma.wmb_api.dto.request.AuthRequest;
import com.enigma.wmb_api.dto.response.AuthResponse;
import com.enigma.wmb_api.dto.response.CommonResponse;
import com.enigma.wmb_api.service.AuthService;
import com.enigma.wmb_api.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

@RestController
@RequestMapping(path = Constant.AUTH_API)
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "APIs for user authentication, token refresh, and logout")
public class AuthController {
    private static class CommonResponseAuthResponse extends CommonResponse<AuthResponse> {}

    private final AuthService authService;

    @Operation(summary = "User login",
            description = "Login to the system using valid credentials. A refresh token will be set in the cookies upon successful login.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = CommonResponseAuthResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid login credentials", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
            })
    @PostMapping(path = "/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request, HttpServletResponse response) {
        AuthResponse authResponse = authService.login(request);
        setCookie(response, authResponse.getRefreshToken());
        return ResponseUtil.buildResponse(HttpStatus.OK, Constant.SUCCESS_LOGIN, authResponse);
    }

    @Operation(summary = "Refresh token",
            description = "Generate a new access token using the refresh token stored in cookies. A new refresh token will be set in the cookies as well.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Token refreshed successfully", content = @Content(schema = @Schema(implementation = CommonResponseAuthResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid or missing refresh token", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
            })
    @PostMapping(path = "/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshTokenFromCookie(request);
        AuthResponse authResponse = authService.refreshToken(refreshToken);
        setCookie(response, authResponse.getRefreshToken());
        return ResponseUtil.buildResponse(HttpStatus.OK, Constant.OK, authResponse);
    }

    @Operation(summary = "Logout",
            description = "Log the user out by invalidating the current access token. No content is returned on successful logout.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Logout successful"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
            })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        authService.logout(bearerToken);
        return ResponseUtil.buildResponse(HttpStatus.OK, Constant.OK, null);
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie cookie = Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals(Constant.REFRESH_TOKEN_COOKIE_NAME))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, Constant.REFRESH_TOKEN_REQUIRED));
        return cookie.getValue();
    }

    private void setCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(Constant.REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(authService.generateTokenExpireInSecond());
        response.addCookie(cookie);
    }
}
