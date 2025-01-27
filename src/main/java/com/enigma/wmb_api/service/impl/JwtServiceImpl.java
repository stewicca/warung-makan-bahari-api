package com.enigma.wmb_api.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.enigma.wmb_api.constant.Constant;
import com.enigma.wmb_api.entity.UserAccount;
import com.enigma.wmb_api.service.JwtService;
import com.enigma.wmb_api.service.RedisService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;


@Service
@Slf4j
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    @Value("${warung.makan.bahari.jwt-secret}")
    private String SECRET_KEY;

    @Value("${warung.makan.bahari.jwt-expiration-in-minutes-access-token}")
    private Long EXPIRATION_IN_MINUTES_ACCESS_TOKEN;

    @Value("${warung.makan.bahari.jwt-issuer}")
    private String ISSUER;

    private final String BLACKLISTED = "BLACKLISTED";

    private final RedisService redisService;

    @Override
    public String generateAccessToken(UserAccount userAccount) {
        log.info("Generating JWT Token for User: {}", userAccount.getId());
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withIssuedAt(Instant.now())
                    .withExpiresAt(Instant.now().plus(EXPIRATION_IN_MINUTES_ACCESS_TOKEN, ChronoUnit.MINUTES))
                    .withSubject(userAccount.getId())
                    .withClaim("role", userAccount.getRole().getDescription())
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            log.error("Error Creating JWT Token: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, Constant.ERROR_CREATE_JWT);
        }
    }

    @Override
    public String getUserId(String token) {
        DecodedJWT decodedJWT = extractClaimJWT(token);
        if (decodedJWT != null) {
            return decodedJWT.getSubject();
        }

        return null;
    }

    @Override
    public String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        return parseToken(bearerToken);
    }

    @Override
    public void blacklistAccessToken(String bearerToken) {
        String token = parseToken(bearerToken);

        DecodedJWT decodedJWT = extractClaimJWT(token);

        if (decodedJWT == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Token invalid");
        }

        Date expiresAt = decodedJWT.getExpiresAt();
        long timeLeft = (expiresAt.getTime() - System.currentTimeMillis());

        redisService.save(token, BLACKLISTED, Duration.ofMillis(timeLeft));
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        String blacklistToken = redisService.get(token);
        return blacklistToken != null && blacklistToken.equals(BLACKLISTED);
    }

    private DecodedJWT extractClaimJWT(String token) {
        log.info("Extract Token JWT - {}", System.currentTimeMillis());
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();
            return verifier.verify(token);
        } catch (JWTVerificationException exception){
            log.error("Error while validate JWT Token: {}", exception.getMessage());
            return null;
        }
    }

    private String parseToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
