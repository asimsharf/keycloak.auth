package com.sudagoarth.keycloak.auth.util;

import java.util.List;

import org.springframework.validation.FieldError;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sudagoarth.keycloak.auth.models.LocaledData;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse(LocaledData message, int status, Object data, String code, boolean success, List<FieldError> errorDetails) {

    // Constructor to create success response
    public static ApiResponse success(LocaledData message, Object data) {
        return new ApiResponse(message, 200, data, "OK", true, null);
    }

    // Constructor to create error response
    public static ApiResponse error(LocaledData message, String code, List<FieldError> errorDetails) {
        return new ApiResponse(message, 400, null, code, false, errorDetails);
    }

    // Builder pattern for flexibility
    public static ApiResponseBuilder builder() {
        return new ApiResponseBuilder();
    }

    // Inner builder class
    public static class ApiResponseBuilder {
        private LocaledData message;
        private int status;
        private Object data;
        private String code;
        private boolean success;
        private List<FieldError>  errorDetails;

        public ApiResponseBuilder message(LocaledData message) {
            this.message = message;
            return this;
        }

        public ApiResponseBuilder status(int status) {
            this.status = status;
            return this;
        }

        public ApiResponseBuilder data(Object data) {
            this.data = data;
            return this;
        }

        public ApiResponseBuilder code(String code) {
            this.code = code;
            return this;
        }

        public ApiResponseBuilder success(boolean success) {
            this.success = success;
            return this;
        }

        public ApiResponseBuilder errorDetails(List<FieldError> errorDetails) {
            this.errorDetails = errorDetails;
            return this;
        }

        public ApiResponse build() {
            return new ApiResponse(message, status, data, code, success, errorDetails);
        }
    }
}
