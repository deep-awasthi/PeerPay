package com.peerpay.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

/**
 * Standardized API response wrapper used across all PeerPay microservices.
 * Ensures consistent response structure for clients.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private String errorCode;
    private LocalDateTime timestamp;
    private String traceId;

    public ApiResponse() {
    }

    public ApiResponse(boolean success, String message, T data, String errorCode, LocalDateTime timestamp, String traceId) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errorCode = errorCode;
        this.timestamp = timestamp;
        this.traceId = traceId;
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, null, LocalDateTime.now(), null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Operation successful");
    }

    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return new ApiResponse<>(false, message, null, errorCode, LocalDateTime.now(), null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return error(message, "INTERNAL_ERROR");
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }

    // Simple Builder Pattern
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private boolean success;
        private String message;
        private T data;
        private String errorCode;
        private LocalDateTime timestamp;
        private String traceId;

        public Builder<T> success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder<T> message(String message) {
            this.message = message;
            return this;
        }

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public Builder<T> errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public Builder<T> timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder<T> traceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public ApiResponse<T> build() {
            return new ApiResponse<>(success, message, data, errorCode, timestamp, traceId);
        }
    }
}
