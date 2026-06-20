package com.peerpay.payment.kafka;

import com.peerpay.payment.model.PaymentOutbox;
import com.peerpay.payment.repository.OutboxRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled component that polls the 'payment_outbox' table and publishes
 * pending events to Apache Kafka, implementing the Transactional Outbox pattern.
 */
@Component
public class OutboxPoller {

    private static final Logger log = LoggerFactory.getLogger(OutboxPoller.class);

    private final OutboxRepository outboxRepository;
    private final PaymentEventProducer eventProducer;
    private final ObjectMapper objectMapper;

    public OutboxPoller(OutboxRepository outboxRepository,
                        PaymentEventProducer eventProducer,
                        ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.eventProducer = eventProducer;
        this.objectMapper = objectMapper;
    }

    /**
     * Polls the outbox database every 500 milliseconds for unprocessed events.
     */
    @Scheduled(fixedDelay = 500)
    @Transactional
    public void pollAndPublish() {
        List<PaymentOutbox> pendingEvents = outboxRepository.findByProcessedFalseOrderByCreatedAtAsc();

        if (pendingEvents.isEmpty()) {
            return;
        }

        log.debug("Found {} pending events in transactional outbox", pendingEvents.size());

        for (PaymentOutbox event : pendingEvents) {
            try {
                String transactionId = extractTransactionId(event.getPayload());
                
                // Publish to Kafka synchronously/asynchronously.
                // In transactional outbox, we can wait for acknowledgement to confirm delivery before updating DB.
                eventProducer.sendEvent(event.getEventType(), transactionId, event.getPayload())
                        .thenAccept(result -> markAsProcessed(event.getId()))
                        .exceptionally(ex -> {
                            log.error("Outbox poller failed to dispatch event ID: {}", event.getId(), ex);
                            return null;
                        });

            } catch (Exception e) {
                log.error("Error processing outbox event ID: {}", event.getId(), e);
            }
        }
    }

    @Transactional
    public void markAsProcessed(Long id) {
        outboxRepository.findById(id).ifPresent(event -> {
            event.setProcessed(true);
            event.setProcessedAt(LocalDateTime.now());
            outboxRepository.save(event);
            log.info("Marked outbox event ID: {} as processed", id);
        });
    }

    private String extractTransactionId(String payload) {
        try {
            JsonNode node = objectMapper.readTree(payload);
            JsonNode txIdNode = node.get("transactionId");
            return txIdNode != null ? txIdNode.asText() : "unknown";
        } catch (Exception e) {
            log.warn("Failed to extract transactionId from event payload", e);
            return "unknown";
        }
    }
}
