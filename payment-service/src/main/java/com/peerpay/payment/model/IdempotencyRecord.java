package com.peerpay.payment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "idempotency_records")
public class IdempotencyRecord {

    @Id
    @Column(name = "idempotency_key", length = 255)
    private String idempotencyKey;

    @Column(name = "response_payload", nullable = false, columnDefinition = "TEXT")
    private String responsePayload;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public IdempotencyRecord() {
    }

    public IdempotencyRecord(String idempotencyKey, String responsePayload, LocalDateTime createdAt) {
        this.idempotencyKey = idempotencyKey;
        this.responsePayload = responsePayload;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }

    public String getResponsePayload() { return responsePayload; }
    public void setResponsePayload(String responsePayload) { this.responsePayload = responsePayload; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String idempotencyKey;
        private String responsePayload;
        private LocalDateTime createdAt;

        public Builder idempotencyKey(String idempotencyKey) {
            this.idempotencyKey = idempotencyKey;
            return this;
        }

        public Builder responsePayload(String responsePayload) {
            this.responsePayload = responsePayload;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public IdempotencyRecord build() {
            return new IdempotencyRecord(idempotencyKey, responsePayload, createdAt);
        }
    }
}
