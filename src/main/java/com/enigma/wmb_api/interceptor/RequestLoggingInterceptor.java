package com.enigma.wmb_api.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Component
@Slf4j
public class RequestLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        String userAgent = request.getHeader("User-Agent");
        String clientIp = getClientIp(request);
        String httpMethod = request.getMethod();
        String requestURI = request.getRequestURI();

        log.info("Endpoint hit: {} | HTTP method: {} | User-Agent: {} | Client IP: {} | Time: {}",
                requestURI, httpMethod, userAgent, clientIp, LocalDateTime.now());

        return true;
    }

    private String getClientIp(HttpServletRequest request) {
        String remoteAddr = request.getHeader("X-FORWARDED-FOR");
        if (remoteAddr == null || remoteAddr.isEmpty()) {
            remoteAddr = request.getRemoteAddr();
        }
        return remoteAddr;
    }
}
