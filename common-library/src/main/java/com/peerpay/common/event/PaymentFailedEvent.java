package com.peerpay.common.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain event published when a payment fails.
 */
public class PaymentFailedEvent {

    private String eventId;
    private String transactionId;
    private String payerUpiId;
    private String payeeUpiId;
    private String payerId;
    private String payeeId;
    private BigDecimal amount;
    private String currency;
    private String failureReason;
    private String errorCode;
    private LocalDateTime initiatedAt;
    private LocalDateTime failedAt;
    private LocalDateTime eventTimestamp;

    public PaymentFailedEvent() {
    }

    public PaymentFailedEvent(String eventId, String transactionId, String payerUpiId, String payeeUpiId, String payerId, String payeeId, BigDecimal amount, String currency, String failureReason, String errorCode, LocalDateTime initiatedAt, LocalDateTime failedAt, LocalDateTime eventTimestamp) {
        this.eventId = eventId;
        this.transactionId = transactionId;
        this.payerUpiId = payerUpiId;
        this.payeeUpiId = payeeUpiId;
        this.payerId = payerId;
        this.payeeId = payeeId;
        this.amount = amount;
        this.currency = currency;
        this.failureReason = failureReason;
        this.errorCode = errorCode;
        this.initiatedAt = initiatedAt;
        this.failedAt = failedAt;
        this.eventTimestamp = eventTimestamp;
    }

    // Getters and Setters
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getPayerUpiId() { return payerUpiId; }
    public void setPayerUpiId(String payerUpiId) { this.payerUpiId = payerUpiId; }

    public String getPayeeUpiId() { return payeeUpiId; }
    public void setPayeeUpiId(String payeeUpiId) { this.payeeUpiId = payeeUpiId; }

    public String getPayerId() { return payerId; }
    public void setPayerId(String payerId) { this.payerId = payerId; }

    public String getPayeeId() { return payeeId; }
    public void setPayeeId(String payeeId) { this.payeeId = payeeId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

    public LocalDateTime getInitiatedAt() { return initiatedAt; }
    public void setInitiatedAt(LocalDateTime initiatedAt) { this.initiatedAt = initiatedAt; }

    public LocalDateTime getFailedAt() { return failedAt; }
    public void setFailedAt(LocalDateTime failedAt) { this.failedAt = failedAt; }

    public LocalDateTime getEventTimestamp() { return eventTimestamp; }
    public void setEventTimestamp(LocalDateTime eventTimestamp) { this.eventTimestamp = eventTimestamp; }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String eventId;
        private String transactionId;
        private String payerUpiId;
        private String payeeUpiId;
        private String payerId;
        private String payeeId;
        private BigDecimal amount;
        private String currency;
        private String failureReason;
        private String errorCode;
        private LocalDateTime initiatedAt;
        private LocalDateTime failedAt;
        private LocalDateTime eventTimestamp;

        public Builder eventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder transactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public Builder payerUpiId(String payerUpiId) {
            this.payerUpiId = payerUpiId;
            return this;
        }

        public Builder payeeUpiId(String payeeUpiId) {
            this.payeeUpiId = payeeUpiId;
            return this;
        }

        public Builder payerId(String payerId) {
            this.payerId = payerId;
            return this;
        }

        public Builder payeeId(String payeeId) {
            this.payeeId = payeeId;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder failureReason(String failureReason) {
            this.failureReason = failureReason;
            return this;
        }

        public Builder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public Builder initiatedAt(LocalDateTime initiatedAt) {
            this.initiatedAt = initiatedAt;
            return this;
        }

        public Builder failedAt(LocalDateTime failedAt) {
            this.failedAt = failedAt;
            return this;
        }

        public Builder eventTimestamp(LocalDateTime eventTimestamp) {
            this.eventTimestamp = eventTimestamp;
            return this;
        }

        public PaymentFailedEvent build() {
            return new PaymentFailedEvent(eventId, transactionId, payerUpiId, payeeUpiId, payerId, payeeId, amount, currency, failureReason, errorCode, initiatedAt, failedAt, eventTimestamp);
        }
    }
}
