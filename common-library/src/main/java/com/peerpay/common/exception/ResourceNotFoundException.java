package com.peerpay.common.exception;

/**
 * Thrown when a requested resource (user, payment, etc.) is not found.
 */
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceType;
    private final String resourceId;

    public ResourceNotFoundException(String resourceType, String resourceId) {
        super(String.format("%s not found with ID: %s", resourceType, resourceId));
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }
}
