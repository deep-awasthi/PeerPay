package com.peerpay.payment.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_outbox")
public class PaymentOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true, length = 36)
    private String eventId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private boolean processed;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    public PaymentOutbox() {
    }

    public PaymentOutbox(Long id, String eventId, String eventType, String payload, boolean processed, LocalDateTime createdAt, LocalDateTime processedAt) {
        this.id = id;
        this.eventId = eventId;
        this.eventType = eventType;
        this.payload = payload;
        this.processed = processed;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }

    public boolean isProcessed() { return processed; }
    public void setProcessed(boolean processed) { this.processed = processed; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String eventId;
        private String eventType;
        private String payload;
        private boolean processed;
        private LocalDateTime createdAt;
        private LocalDateTime processedAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder eventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder eventType(String eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder payload(String payload) {
            this.payload = payload;
            return this;
        }

        public Builder processed(boolean processed) {
            this.processed = processed;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder processedAt(LocalDateTime processedAt) {
            this.processedAt = processedAt;
            return this;
        }

        public PaymentOutbox build() {
            return new PaymentOutbox(id, eventId, eventType, payload, processed, createdAt, processedAt);
        }
    }
}
