package com.peerpay.reconciliation.kafka;

import com.peerpay.common.constants.KafkaTopics;
import com.peerpay.reconciliation.model.DlqRecord;
import com.peerpay.reconciliation.repository.DlqRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DlqConsumer {

    private static final Logger log = LoggerFactory.getLogger(DlqConsumer.class);

    private final DlqRecordRepository repository;

    public DlqConsumer(DlqRecordRepository repository) {
        this.repository = repository;
    }

    /**
     * Consumes dead letter queue (DLQ) messages for review.
     */
    @KafkaListener(
            topics = KafkaTopics.PAYMENT_DLQ,
            groupId = KafkaTopics.RECONCILIATION_GROUP
    )
    public void consumeDlq(
            @Payload String payload,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key,
            @Header(value = KafkaHeaders.RECEIVED_TOPIC, required = false) String topic,
            @Header(value = "x-original-topic", defaultValue = "unknown") String originalTopic,
            @Header(value = "x-exception-message", defaultValue = "No error message provided") String exceptionMessage) {

        log.warn("⚠️ [DLQ RECEIVED] Message on topic: {} (original topic: {}) | Key: {} | Error: {}",
                topic, originalTopic, key, exceptionMessage);

        DlqRecord record = DlqRecord.builder()
                .originalTopic(originalTopic)
                .messageKey(key)
                .payload(payload)
                .errorMessage(exceptionMessage.length() > 1000 ? exceptionMessage.substring(0, 997) + "..." : exceptionMessage)
                .receivedAt(LocalDateTime.now())
                .resolved(false)
                .build();

        repository.save(record);
        log.info("Saved DLQ poison pill to DB for manual audit. Record ID: {}", record.getId());
    }
}
