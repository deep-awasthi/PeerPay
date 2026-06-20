package com.peerpay.common.constants;

/**
 * Central registry of Kafka topic names used across PeerPay microservices.
 * Prevents magic strings and ensures consistency.
 */
public final class KafkaTopics {

    private KafkaTopics() {
        // Utility class – prevent instantiation
    }

    /** Published by Payment Service when a payment is initiated */
    public static final String PAYMENT_INITIATED = "peerpay.payments.initiated";

    /** Published by Payment Service when a payment is successfully completed */
    public static final String PAYMENT_COMPLETED = "peerpay.payments.completed";

    /** Published by Payment Service when a payment fails */
    public static final String PAYMENT_FAILED = "peerpay.payments.failed";

    /** Dead Letter Queue for messages that failed processing after retries */
    public static final String PAYMENT_DLQ = "peerpay.payments.dlq";

    /** Consumer group IDs */
    public static final String LEDGER_GROUP = "peerpay-ledger-group";
    public static final String NOTIFICATION_GROUP = "peerpay-notification-group";
    public static final String RECONCILIATION_GROUP = "peerpay-reconciliation-group";
}
