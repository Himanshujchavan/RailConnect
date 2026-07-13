package com.railconnect.common.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Phase 10 — API Versioning.
 * <p>
 * This API versions by URI path: every controller is mounted under {@code /api/v1/...} (see
 * {@code common.constants.APIConstants.API_V1_PREFIX}). A breaking change gets a new
 * {@code /api/v2} controller package added alongside v1 rather than modifying v1 in place, so
 * existing clients keep working against {@code /api/v1} unmodified until they choose to move.
 * <p>
 * This filter just echoes the currently-served version back on every response as
 * {@code X-API-Version}, so a client (or this API's own logs) can always confirm which version
 * actually handled a request without it being implicit in the URL alone.
 */
@Component
@Order(Integer.MIN_VALUE + 30)
public class ApiVersionHeaderFilter extends OncePerRequestFilter {

    @Value("${api.version:v1}")
    private String apiVersion;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        response.setHeader("X-API-Version", apiVersion);
        chain.doFilter(request, response);
    }
}
