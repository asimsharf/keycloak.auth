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

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse> handleGeneralException(Exception ex) {
                logger.error("Unhandled exception: ", ex);

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiResponse.error(new LocaledData(
                                                "An unexpected error occurred.",
                                                "حدث خطأ غير متوقع"),
                                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                                "INTERNAL_SERVER_ERROR", null));
        }

        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<ApiResponse> handleAuthenticationException(AuthenticationException ex) {
                logger.warn("Authentication failed: {}", ex.getMessage());

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(ApiResponse.error(new LocaledData(
                                                "Invalid authentication credentials.",
                                                "بيانات المصادقة غير صالحة"),
                                                HttpStatus.UNAUTHORIZED.value(),
                                                "UNAUTHORIZED", null));
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ApiResponse> handleAccessDeniedException(AccessDeniedException ex) {
                logger.warn("Access denied: {}", ex.getMessage());

                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(ApiResponse.error(new LocaledData(
                                                "You do not have permission to perform this action.",
                                                "ليس لديك إذن للقيام بهذا الإجراء"),
                                                HttpStatus.FORBIDDEN.value(),
                                                "ACCESS_DENIED", null));
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse> handleValidationException(MethodArgumentNotValidException ex) {
                BindingResult result = ex.getBindingResult();
                List<FieldError> errors = result.getFieldErrors();

                logger.warn("Validation failed: {}", errors);

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error(new LocaledData(
                                                "Validation failed. Please check your input.",
                                                "فشل التحقق. يرجى التحقق من المدخلات"),
                                                HttpStatus.BAD_REQUEST.value(),
                                                "VALIDATION_ERROR", errors));
        }

        @ExceptionHandler(ProductNotFoundException.class)
        public ResponseEntity<ApiResponse> handleProductNotFound(ProductNotFoundException ex) {
                logger.warn("Product not found: {}", ex.getMessage());

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(ApiResponse.error(new LocaledData(
                                                ex.getMessage() != null ? ex.getMessage() : "Product not found",
                                                "المنتج غير موجود"),
                                                HttpStatus.NOT_FOUND.value(), "NOT_FOUND", null));
        }
}
