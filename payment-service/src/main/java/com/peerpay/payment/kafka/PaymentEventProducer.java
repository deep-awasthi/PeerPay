package com.peerpay.payment.kafka;

import com.peerpay.common.constants.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class PaymentEventProducer {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    public PaymentEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publishes a serialized event to a specified Kafka topic with transactionId as the message key.
     * Ensuring key partitioning so events for a single transaction preserve order.
     */
    public CompletableFuture<SendResult<String, String>> sendEvent(String eventType, String transactionId, String payload) {
        String topic = determineTopic(eventType);
        log.info("Publishing event {} to topic {} for transaction ID: {}", eventType, topic, transactionId);

        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, transactionId, payload);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Successfully published event {} for transaction {}. Offset: {}",
                        eventType, transactionId, result.getRecordMetadata().offset());
            } else {
                log.error("Failed to publish event {} for transaction {}.", eventType, transactionId, ex);
            }
        });

        return future;
    }

    private String determineTopic(String eventType) {
        return switch (eventType) {
            case "PaymentInitiated" -> KafkaTopics.PAYMENT_INITIATED;
            case "PaymentCompleted" -> KafkaTopics.PAYMENT_COMPLETED;
            case "PaymentFailed" -> KafkaTopics.PAYMENT_FAILED;
            default -> throw new IllegalArgumentException("Unknown event type: " + eventType);
        };
    }
}
