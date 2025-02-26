package com.sudagoarth.keycloak.auth.exceptions;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sudagoarth.keycloak.auth.models.LocaledData;
import com.sudagoarth.keycloak.auth.util.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
public class ApiAuthentication implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(ApiAuthentication.class);
    private final ObjectMapper objectMapper;

    public ApiAuthentication(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {

        logger.error("Unauthorized access: {}, Path: {}", authException.getMessage(), request.getRequestURI());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        List<FieldError> errors = List.of(new FieldError("auth", "token", authException.getMessage()));

        ApiResponse errorResponse = ApiResponse.error(new LocaledData(
                "Unauthorized access. Please check your authentication token.",
                "الوصول غير مصرح به. يرجى التحقق من رمز المصادقة الخاص بك."),
                HttpServletResponse.SC_UNAUTHORIZED,
                "UNAUTHORIZED", errors);

        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush(); // ✅ Force flush response buffer
    }

}
