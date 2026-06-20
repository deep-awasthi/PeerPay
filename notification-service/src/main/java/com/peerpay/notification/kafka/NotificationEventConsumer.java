package com.peerpay.notification.kafka;

import com.peerpay.common.constants.KafkaTopics;
import com.peerpay.common.event.PaymentCompletedEvent;
import com.peerpay.common.event.PaymentFailedEvent;
import com.peerpay.common.event.PaymentInitiatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventConsumer.class);

    private final ObjectMapper objectMapper;

    public NotificationEventConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Listens to payment initiation events.
     */
    @KafkaListener(
            topics = KafkaTopics.PAYMENT_INITIATED,
            groupId = KafkaTopics.NOTIFICATION_GROUP,
            properties = {"spring.json.value.default.type=java.lang.String"}
    )
    public void consumePaymentInitiated(String message) {
        log.info("Received PaymentInitiated event from Kafka");
        try {
            PaymentInitiatedEvent event = objectMapper.readValue(message, PaymentInitiatedEvent.class);
            log.info("🔔 [NOTIFICATION SENT] SMS to payer ({}): Payment of ₹{} to {} is INITIATED. Txn ID: {}",
                    event.getPayerUpiId(), event.getAmount(), event.getPayeeUpiId(), event.getTransactionId());
        } catch (Exception e) {
            log.error("Failed to parse PaymentInitiatedEvent: {}", message, e);
        }
    }

    /**
     * Listens to payment completion events.
     */
    @KafkaListener(
            topics = KafkaTopics.PAYMENT_COMPLETED,
            groupId = KafkaTopics.NOTIFICATION_GROUP,
            properties = {"spring.json.value.default.type=java.lang.String"}
    )
    public void consumePaymentCompleted(String message) {
        log.info("Received PaymentCompleted event from Kafka");
        try {
            PaymentCompletedEvent event = objectMapper.readValue(message, PaymentCompletedEvent.class);
            log.info("🔔 [NOTIFICATION SENT] SMS to payer ({}): Payment of ₹{} to {} is SUCCESSFUL. Txn ID: {}",
                    event.getPayerUpiId(), event.getAmount(), event.getPayeeUpiId(), event.getTransactionId());
            log.info("🔔 [NOTIFICATION SENT] SMS to payee ({}): Account credited with ₹{} from {}. Txn ID: {}",
                    event.getPayeeUpiId(), event.getAmount(), event.getPayerUpiId(), event.getTransactionId());
        } catch (Exception e) {
            log.error("Failed to parse PaymentCompletedEvent: {}", message, e);
        }
    }

    /**
     * Listens to payment failure events.
     */
    @KafkaListener(
            topics = KafkaTopics.PAYMENT_FAILED,
            groupId = KafkaTopics.NOTIFICATION_GROUP,
            properties = {"spring.json.value.default.type=java.lang.String"}
    )
    public void consumePaymentFailed(String message) {
        log.info("Received PaymentFailed event from Kafka");
        try {
            PaymentFailedEvent event = objectMapper.readValue(message, PaymentFailedEvent.class);
            log.warn("🔔 [NOTIFICATION SENT] SMS to payer ({}): Payment of ₹{} to {} failed. Reason: {}. Txn ID: {}",
                    event.getPayerUpiId(), event.getAmount(), event.getPayeeUpiId(), event.getFailureReason(), event.getTransactionId());
        } catch (Exception e) {
            log.error("Failed to parse PaymentFailedEvent: {}", message, e);
        }
    }
}
