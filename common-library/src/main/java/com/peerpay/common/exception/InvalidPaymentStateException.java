package com.peerpay.common.exception;

/**
 * Thrown when a payment state transition is invalid.
 * E.g., trying to move from COMPLETED → PROCESSING.
 */
public class InvalidPaymentStateException extends RuntimeException {

    private final String currentState;
    private final String requestedState;

    public InvalidPaymentStateException(String currentState, String requestedState) {
        super(String.format("Invalid state transition: %s → %s", currentState, requestedState));
        this.currentState = currentState;
        this.requestedState = requestedState;
    }

    public String getCurrentState() {
        return currentState;
    }

    public String getRequestedState() {
        return requestedState;
    }
}
