package com.sudagoarth.keycloak.auth.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sudagoarth.keycloak.auth.exceptions.products.ProductNotFoundException;
import com.sudagoarth.keycloak.auth.models.LocaledData;
import com.sudagoarth.keycloak.auth.util.ApiResponse;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle generic exceptions (fallback).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneralException(Exception ex) {
        logger.error("Unhandled exception: ", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(new LocaledData(
                        "An unexpected error occurred.",
                        "حدث خطأ غير متوقع"), "INTERNAL_SERVER_ERROR", null));
    }

    /**
     * Handle authentication errors (e.g., invalid credentials).
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse> handleAuthenticationException(AuthenticationException ex) {
        logger.warn("Authentication failed: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(new LocaledData(
                        "Invalid authentication credentials.",
                        "بيانات المصادقة غير صالحة"), "UNAUTHORIZED", null));
    }

    /**
     * Handle authorization errors (e.g., insufficient permissions).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDeniedException(AccessDeniedException ex) {
        logger.warn("Access denied: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(new LocaledData(
                        "You do not have permission to perform this action.",
                        "ليس لديك إذن للقيام بهذا الإجراء"), "ACCESS_DENIED", null));
    }

    /**
     * Handle validation errors (e.g., invalid request data).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();

        List<FieldError> errors = result.getFieldErrors();

        logger.warn("Validation failed: {}", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(new LocaledData(
                        "Validation failed. Please check your input.",
                        "فشل التحقق. يرجى التحقق من المدخلات"), "VALIDATION_ERROR", errors));
    }

    /**
     * Handle not found exceptions (e.g., missing products).
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiResponse> handleProductNotFound(ProductNotFoundException ex) {
        logger.warn("Product not found: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(new LocaledData(
                        ex.getMessage() != null ? ex.getMessage() : "Product not found",
                        "المنتج غير موجود"), "NOT_FOUND", null));
    }

}
