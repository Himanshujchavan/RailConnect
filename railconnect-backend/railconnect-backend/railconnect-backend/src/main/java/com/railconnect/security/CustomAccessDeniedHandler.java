package com.railconnect.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.railconnect.common.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handles 403s (authenticated, but insufficient role) with the same ErrorResponse
 * shape CustomAuthenticationEntryPoint uses for 401s, so the two failure modes
 * look consistent to API consumers.
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request,
                        HttpServletResponse response,
                        AccessDeniedException accessDeniedException) throws IOException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());

        ErrorResponse errorBody = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Access denied",
                "You do not have permission to access this resource"
        );

        objectMapper.writeValue(response.getOutputStream(), errorBody);
    }
}
