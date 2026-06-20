package com.peerpay.common.exception;

/**
 * Thrown when an optimistic lock conflict is detected (concurrent modification).
 */
public class ConcurrencyConflictException extends RuntimeException {

    private final String entityType;
    private final String entityId;

    public ConcurrencyConflictException(String entityType, String entityId) {
        super(String.format("Concurrent modification detected for %s with ID: %s. Please retry.", entityType, entityId));
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getEntityId() {
        return entityId;
    }
}
