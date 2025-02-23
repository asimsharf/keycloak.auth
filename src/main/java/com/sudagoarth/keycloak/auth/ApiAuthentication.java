package com.sudagoarth.keycloak.auth;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class ApiAuthentication implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(ApiAuthentication.class);
    private final ObjectMapper objectMapper;

    public ApiAuthentication(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper; // Dependency Injection
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {

        String message = "Unauthorized access. Please check your authentication token.";
        String code = "UNAUTHORIZED";
        int status = HttpServletResponse.SC_UNAUTHORIZED;
        String errorDetails = authException.getMessage();

        if (authException.getMessage().contains("JWT")) {
            errorDetails = "JWT authentication failed";
        } else if (authException.getMessage().contains("Bearer")) {
            errorDetails = "Bearer token authentication failed";
        } else if (authException.getMessage().contains("Basic")) {
            errorDetails = "Basic authentication failed";
        }

        // Log the authentication failure
        logger.error("Authentication failed: {}", errorDetails);

        // Create the ApiResponse
        ApiResponse apiResponse = new ApiResponse(
                message, status, null, code, false, errorDetails);

        // Send the response
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
