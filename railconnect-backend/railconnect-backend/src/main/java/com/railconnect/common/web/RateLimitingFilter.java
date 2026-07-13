package com.railconnect.common.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Phase 10 — Rate Limiting.
 * <p>
 * Simple fixed-window counter per client key (client IP by default, or the value of an
 * {@code X-API-Key} header if one is sent - useful once this sits behind an API gateway that
 * issues per-consumer keys). Deliberately in-memory and dependency-free rather than pulling in
 * a library (e.g. Bucket4j) whose Maven coordinates couldn't be verified from this environment.
 * <p>
 * Single-instance limitation: like {@code SseEmitterRegistry}, these counters live in this
 * instance's memory only. Behind a load balancer with multiple app instances, each instance
 * enforces its own limit independently (so the effective limit is roughly
 * {@code requestsPerWindow * instanceCount}) - a real multi-instance deployment should move
 * this counting into Redis (e.g. INCR + EXPIRE) instead.
 */
@Component
@Order(Integer.MIN_VALUE + 20)
public class RateLimitingFilter extends OncePerRequestFilter {

    private final RateLimitProperties properties;
    private final ConcurrentHashMap<String, Window> windowsByClient = new ConcurrentHashMap<>();

    public RateLimitingFilter(RateLimitProperties properties) {
        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if (!properties.isEnabled()) {
            chain.doFilter(request, response);
            return;
        }

        String clientKey = resolveClientKey(request);
        Window window = windowsByClient.computeIfAbsent(clientKey, key -> new Window(currentWindowStart()));

        long windowStart = currentWindowStart();
        int countInWindow;
        synchronized (window) {
            if (window.windowStartMillis != windowStart) {
                window.windowStartMillis = windowStart;
                window.count.set(0);
            }
            countInWindow = window.count.incrementAndGet();
        }

        int limit = properties.getRequestsPerWindow();
        response.setHeader("X-RateLimit-Limit", String.valueOf(limit));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(limit - countInWindow, 0)));

        if (countInWindow > limit) {
            response.setStatus(429); // 429 Too Many Requests
            response.setHeader("Retry-After", String.valueOf(properties.getWindowSeconds()));
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"status\":429,\"error\":\"Too Many Requests\","
                            + "\"message\":\"Rate limit exceeded - try again shortly.\"}");
            return;
        }

        chain.doFilter(request, response);
    }

    private long currentWindowStart() {
        long windowMillis = properties.getWindowSeconds() * 1000L;
        return (System.currentTimeMillis() / windowMillis) * windowMillis;
    }

    private String resolveClientKey(HttpServletRequest request) {
        String apiKey = request.getHeader("X-API-Key");
        if (apiKey != null && !apiKey.isBlank()) {
            return "key:" + apiKey;
        }

        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return "ip:" + forwardedFor.split(",")[0].trim();
        }

        return "ip:" + request.getRemoteAddr();
    }

    private static final class Window {
        private volatile long windowStartMillis;
        private final AtomicInteger count = new AtomicInteger(0);

        private Window(long windowStartMillis) {
            this.windowStartMillis = windowStartMillis;
        }
    }
}
