package com.peerpay.reconciliation.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dlq_records")
public class DlqRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_topic", nullable = false)
    private String originalTopic;

    @Column(name = "message_key")
    private String messageKey;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;

    @Column(nullable = false)
    private boolean resolved;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    public DlqRecord() {
    }

    public DlqRecord(Long id, String originalTopic, String messageKey, String payload, String errorMessage, LocalDateTime receivedAt, boolean resolved, LocalDateTime resolvedAt) {
        this.id = id;
        this.originalTopic = originalTopic;
        this.messageKey = messageKey;
        this.payload = payload;
        this.errorMessage = errorMessage;
        this.receivedAt = receivedAt;
        this.resolved = resolved;
        this.resolvedAt = resolvedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOriginalTopic() { return originalTopic; }
    public void setOriginalTopic(String originalTopic) { this.originalTopic = originalTopic; }

    public String getMessageKey() { return messageKey; }
    public void setMessageKey(String messageKey) { this.messageKey = messageKey; }

    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public LocalDateTime getReceivedAt() { return receivedAt; }
    public void setReceivedAt(LocalDateTime receivedAt) { this.receivedAt = receivedAt; }

    public boolean isResolved() { return resolved; }
    public void setResolved(boolean resolved) { this.resolved = resolved; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String originalTopic;
        private String messageKey;
        private String payload;
        private String errorMessage;
        private LocalDateTime receivedAt;
        private boolean resolved;
        private LocalDateTime resolvedAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder originalTopic(String originalTopic) {
            this.originalTopic = originalTopic;
            return this;
        }

        public Builder messageKey(String messageKey) {
            this.messageKey = messageKey;
            return this;
        }

        public Builder payload(String payload) {
            this.payload = payload;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public Builder receivedAt(LocalDateTime receivedAt) {
            this.receivedAt = receivedAt;
            return this;
        }

        public Builder resolved(boolean resolved) {
            this.resolved = resolved;
            return this;
        }

        public Builder resolvedAt(LocalDateTime resolvedAt) {
            this.resolvedAt = resolvedAt;
            return this;
        }

        public DlqRecord build() {
            return new DlqRecord(id, originalTopic, messageKey, payload, errorMessage, receivedAt, resolved, resolvedAt);
        }
    }
}
