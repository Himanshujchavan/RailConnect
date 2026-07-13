package com.railconnect.common.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Phase 10 — Logging.
 * <p>
 * Assigns a short correlation id to every request (reusing an inbound {@code X-Request-Id} if
 * the caller/gateway already set one) so every log line touched while handling that request can
 * be found with one grep, and logs a single summary line per request on the way out (method,
 * path, status, duration). The pattern in application.yml's {@code logging.pattern.console}
 * pulls this out of MDC as {@code reqId=...}.
 */
@Component
@Order(Integer.MIN_VALUE + 10)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger("com.railconnect.access");
    private static final String REQUEST_ID_HEADER = "X-Request-Id";
    private static final String MDC_KEY = "requestId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString().substring(0, 8);
        }

        MDC.put(MDC_KEY, requestId);
        response.setHeader(REQUEST_ID_HEADER, requestId);

        long startedAt = System.currentTimeMillis();
        try {
            chain.doFilter(request, response);
        } finally {
            long durationMillis = System.currentTimeMillis() - startedAt;
            logger.info("{} {} -> {} ({} ms)",
                    request.getMethod(), request.getRequestURI(), response.getStatus(), durationMillis);
            MDC.remove(MDC_KEY);
        }
    }
}
