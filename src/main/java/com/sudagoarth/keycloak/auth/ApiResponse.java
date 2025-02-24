package com.sudagoarth.keycloak.auth;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse( String message, int status, Object data, String code, boolean success, String errorDetails) {

    // Constructor to create success response
    public static ApiResponse success(String message, Object data) {
        return new ApiResponse(message, 200, data, "OK", true, null);
    }

    // Constructor to create error response
    public static ApiResponse error(String message, String code, String errorDetails) {
        return new ApiResponse(message, 500, null, code, false, errorDetails);
    }

    // Builder pattern for flexibility
    public static ApiResponseBuilder builder() {
        return new ApiResponseBuilder();
    }

    // Inner builder class
    public static class ApiResponseBuilder {
        private String message;
        private int status;
        private Object data;
        private String code;
        private boolean success;
        private String errorDetails;

        public ApiResponseBuilder message(String message) {
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

        public ApiResponseBuilder errorDetails(String errorDetails) {
            this.errorDetails = errorDetails;
            return this;
        }

        public ApiResponse build() {
            return new ApiResponse(message, status, data, code, success, errorDetails);
        }
    }
}
