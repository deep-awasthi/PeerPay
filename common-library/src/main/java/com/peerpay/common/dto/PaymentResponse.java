package com.peerpay.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response payload returned after payment operations.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponse {

    private String transactionId;
    private String payerUpiId;
    private String payeeUpiId;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String remarks;
    private LocalDateTime initiatedAt;
    private LocalDateTime completedAt;

    public PaymentResponse() {
    }

    public PaymentResponse(String transactionId, String payerUpiId, String payeeUpiId, BigDecimal amount, String currency, String status, String remarks, LocalDateTime initiatedAt, LocalDateTime completedAt) {
        this.transactionId = transactionId;
        this.payerUpiId = payerUpiId;
        this.payeeUpiId = payeeUpiId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.remarks = remarks;
        this.initiatedAt = initiatedAt;
        this.completedAt = completedAt;
    }

    // Getters and Setters
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getPayerUpiId() { return payerUpiId; }
    public void setPayerUpiId(String payerUpiId) { this.payerUpiId = payerUpiId; }

    public String getPayeeUpiId() { return payeeUpiId; }
    public void setPayeeUpiId(String payeeUpiId) { this.payeeUpiId = payeeUpiId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDateTime getInitiatedAt() { return initiatedAt; }
    public void setInitiatedAt(LocalDateTime initiatedAt) { this.initiatedAt = initiatedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String transactionId;
        private String payerUpiId;
        private String payeeUpiId;
        private BigDecimal amount;
        private String currency;
        private String status;
        private String remarks;
        private LocalDateTime initiatedAt;
        private LocalDateTime completedAt;

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

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder remarks(String remarks) {
            this.remarks = remarks;
            return this;
        }

        public Builder initiatedAt(LocalDateTime initiatedAt) {
            this.initiatedAt = initiatedAt;
            return this;
        }

        public Builder completedAt(LocalDateTime completedAt) {
            this.completedAt = completedAt;
            return this;
        }

        public PaymentResponse build() {
            return new PaymentResponse(transactionId, payerUpiId, payeeUpiId, amount, currency, status, remarks, initiatedAt, completedAt);
        }
    }
}
