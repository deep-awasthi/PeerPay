package com.peerpay.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peerpay.common.dto.PaymentResponse;
import com.peerpay.payment.model.IdempotencyRecord;
import com.peerpay.payment.repository.IdempotencyRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class IdempotencyService {

    private static final Logger log = LoggerFactory.getLogger(IdempotencyService.class);
    private static final String REDIS_PREFIX = "payment_idempotency:";
    private static final Duration TTL = Duration.ofHours(24);

    private final StringRedisTemplate redisTemplate;
    private final IdempotencyRecordRepository repository;
    private final ObjectMapper objectMapper;

    public IdempotencyService(StringRedisTemplate redisTemplate,
                              IdempotencyRecordRepository repository,
                              ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    /**
     * Checks if a request has already been processed using the idempotency key.
     * Checks Redis first, then the PostgreSQL database.
     */
    public Optional<PaymentResponse> getResponse(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return Optional.empty();
        }

        String redisKey = REDIS_PREFIX + idempotencyKey;

        // Check Redis
        try {
            String cachedResponse = redisTemplate.opsForValue().get(redisKey);
            if (cachedResponse != null) {
                log.info("Idempotency hit in Redis for key: {}", idempotencyKey);
                return Optional.of(objectMapper.readValue(cachedResponse, PaymentResponse.class));
            }
        } catch (Exception e) {
            log.warn("Failed to check Redis for idempotency key: {}", idempotencyKey, e);
        }

        // Check Database
        Optional<IdempotencyRecord> recordOpt = repository.findById(idempotencyKey);
        if (recordOpt.isPresent()) {
            log.info("Idempotency hit in Database for key: {}", idempotencyKey);
            String responsePayload = recordOpt.get().getResponsePayload();
            
            // Populating cache for future checks
            try {
                redisTemplate.opsForValue().set(redisKey, responsePayload, TTL);
            } catch (Exception e) {
                log.warn("Failed to populate Redis cache for key: {}", idempotencyKey, e);
            }

            try {
                return Optional.of(objectMapper.readValue(responsePayload, PaymentResponse.class));
            } catch (JsonProcessingException e) {
                log.error("Failed to parse idempotency response payload", e);
            }
        }

        return Optional.empty();
    }

    /**
     * Saves the response for a successful transaction process in both DB and Redis cache.
     * Runs in a separate transaction propagation so it doesn't fail if the parent transaction rolls back,
     * or vice versa, but here we run it as part of the overall transaction to guarantee atomic persistence.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveResponse(String idempotencyKey, PaymentResponse response) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return;
        }

        try {
            String payload = objectMapper.writeValueAsString(response);

            // Save to DB
            IdempotencyRecord record = IdempotencyRecord.builder()
                    .idempotencyKey(idempotencyKey)
                    .responsePayload(payload)
                    .createdAt(LocalDateTime.now())
                    .build();
            repository.save(record);

            // Save to Redis
            String redisKey = REDIS_PREFIX + idempotencyKey;
            redisTemplate.opsForValue().set(redisKey, payload, TTL);

            log.info("Saved idempotency record for key: {}", idempotencyKey);
        } catch (Exception e) {
            log.error("Failed to save idempotency response for key: {}", idempotencyKey, e);
        }
    }
}
