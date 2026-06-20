package com.peerpay.common.exception;

/**
 * Thrown when a payment request with the same idempotency key has already been processed.
 */
public class DuplicateRequestException extends RuntimeException {

    private final String idempotencyKey;

    public DuplicateRequestException(String idempotencyKey) {
        super("Duplicate request detected for idempotency key: " + idempotencyKey);
        this.idempotencyKey = idempotencyKey;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }
}
