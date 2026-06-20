package com.peerpay.payment.model;

/**
 * Lifecycle states of a payment transaction.
 */
public enum PaymentStatus {
    INITIATED,
    PROCESSING,
    COMPLETED,
    FAILED,
    REFUND_INITIATED,
    REFUNDED
}
