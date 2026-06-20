package com.peerpay.common.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Request payload for initiating a UPI payment.
 */
public class PaymentRequest {

    @NotBlank(message = "Payer UPI ID is required")
    private String payerUpiId;

    @NotBlank(message = "Payee UPI ID is required")
    private String payeeUpiId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", message = "Minimum transaction amount is ₹1.00")
    private BigDecimal amount;

    private String currency;
    private String remarks;

    @NotBlank(message = "Payer ID is required")
    private String payerId;

    @NotBlank(message = "Payee ID is required")
    private String payeeId;

    public PaymentRequest() {
    }

    public PaymentRequest(String payerUpiId, String payeeUpiId, BigDecimal amount, String currency, String remarks, String payerId, String payeeId) {
        this.payerUpiId = payerUpiId;
        this.payeeUpiId = payeeUpiId;
        this.amount = amount;
        this.currency = currency;
        this.remarks = remarks;
        this.payerId = payerId;
        this.payeeId = payeeId;
    }

    // Getters and Setters
    public String getPayerUpiId() { return payerUpiId; }
    public void setPayerUpiId(String payerUpiId) { this.payerUpiId = payerUpiId; }

    public String getPayeeUpiId() { return payeeUpiId; }
    public void setPayeeUpiId(String payeeUpiId) { this.payeeUpiId = payeeUpiId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getPayerId() { return payerId; }
    public void setPayerId(String payerId) { this.payerId = payerId; }

    public String getPayeeId() { return payeeId; }
    public void setPayeeId(String payeeId) { this.payeeId = payeeId; }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String payerUpiId;
        private String payeeUpiId;
        private BigDecimal amount;
        private String currency;
        private String remarks;
        private String payerId;
        private String payeeId;

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

        public Builder remarks(String remarks) {
            this.remarks = remarks;
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

        public PaymentRequest build() {
            return new PaymentRequest(payerUpiId, payeeUpiId, amount, currency, remarks, payerId, payeeId);
        }
    }
}
