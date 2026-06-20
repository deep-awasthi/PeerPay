package com.peerpay.payment.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @Column(name = "transaction_id", length = 36)
    private String transactionId;

    @Column(name = "payer_id", nullable = false)
    private String payerId;

    @Column(name = "payee_id", nullable = false)
    private String payeeId;

    @Column(name = "payer_upi_id", nullable = false)
    private String payerUpiId;

    @Column(name = "payee_upi_id", nullable = false)
    private String payeeUpiId;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(length = 255)
    private String remarks;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "initiated_at", nullable = false)
    private LocalDateTime initiatedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    @Column(nullable = false)
    private Long version;

    public Payment() {
    }

    public Payment(String transactionId, String payerId, String payeeId, String payerUpiId, String payeeUpiId, BigDecimal amount, String currency, PaymentStatus status, String remarks, String failureReason, String errorCode, LocalDateTime initiatedAt, LocalDateTime updatedAt, Long version) {
        this.transactionId = transactionId;
        this.payerId = payerId;
        this.payeeId = payeeId;
        this.payerUpiId = payerUpiId;
        this.payeeUpiId = payeeUpiId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.remarks = remarks;
        this.failureReason = failureReason;
        this.errorCode = errorCode;
        this.initiatedAt = initiatedAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    // Getters and Setters
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getPayerId() { return payerId; }
    public void setPayerId(String payerId) { this.payerId = payerId; }

    public String getPayeeId() { return payeeId; }
    public void setPayeeId(String payeeId) { this.payeeId = payeeId; }

    public String getPayerUpiId() { return payerUpiId; }
    public void setPayerUpiId(String payerUpiId) { this.payerUpiId = payerUpiId; }

    public String getPayeeUpiId() { return payeeUpiId; }
    public void setPayeeUpiId(String payeeUpiId) { this.payeeUpiId = payeeUpiId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

    public LocalDateTime getInitiatedAt() { return initiatedAt; }
    public void setInitiatedAt(LocalDateTime initiatedAt) { this.initiatedAt = initiatedAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String transactionId;
        private String payerId;
        private String payeeId;
        private String payerUpiId;
        private String payeeUpiId;
        private BigDecimal amount;
        private String currency;
        private PaymentStatus status;
        private String remarks;
        private String failureReason;
        private String errorCode;
        private LocalDateTime initiatedAt;
        private LocalDateTime updatedAt;
        private Long version;

        public Builder transactionId(String transactionId) {
            this.transactionId = transactionId;
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

        public Builder payerUpiId(String payerUpiId) {
            this.payerUpiId = payerUpiId;
            return this;
        }

        public Builder payeeUpiId(String payeeUpiId) {
            this.payeeUpiId = payeeUpiId;
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

        public Builder status(PaymentStatus status) {
            this.status = status;
            return this;
        }

        public Builder remarks(String remarks) {
            this.remarks = remarks;
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

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder version(Long version) {
            this.version = version;
            return this;
        }

        public Payment build() {
            return new Payment(transactionId, payerId, payeeId, payerUpiId, payeeUpiId, amount, currency, status, remarks, failureReason, errorCode, initiatedAt, updatedAt, version);
        }
    }
}
