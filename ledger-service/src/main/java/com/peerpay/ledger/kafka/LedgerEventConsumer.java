package com.peerpay.ledger.kafka;

import com.peerpay.common.constants.KafkaTopics;
import com.peerpay.common.event.PaymentCompletedEvent;
import com.peerpay.ledger.service.LedgerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class LedgerEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(LedgerEventConsumer.class);

    private final LedgerService ledgerService;
    private final ObjectMapper objectMapper;

    public LedgerEventConsumer(LedgerService ledgerService, ObjectMapper objectMapper) {
        this.ledgerService = ledgerService;
        this.objectMapper = objectMapper;
    }

    /**
     * Consumes successful payment completion events to record double-entry balances.
     */
    @KafkaListener(
            topics = KafkaTopics.PAYMENT_COMPLETED,
            groupId = KafkaTopics.LEDGER_GROUP,
            properties = {"spring.json.value.default.type=java.lang.String"}
    )
    public void consumePaymentCompleted(String message) {
        log.info("Received PaymentCompleted event from Kafka: {}", message);

        try {
            PaymentCompletedEvent event = objectMapper.readValue(message, PaymentCompletedEvent.class);
            
            ledgerService.recordTransaction(
                    event.getTransactionId(),
                    event.getPayerId(),
                    event.getPayeeId(),
                    event.getAmount(),
                    event.getCurrency()
            );

        } catch (Exception e) {
            log.error("Failed to process payment completion event. Message details: {}", message, e);
            // In a production app, we would throw the exception to trigger the retry/DLQ mechanism,
            // or explicitly route to DLQ ourselves.
            throw new RuntimeException("Error processing Ledger event", e);
        }
    }
}
