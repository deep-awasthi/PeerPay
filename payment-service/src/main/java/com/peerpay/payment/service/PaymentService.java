package com.peerpay.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peerpay.common.dto.PaymentRequest;
import com.peerpay.common.dto.PaymentResponse;
import com.peerpay.common.event.PaymentCompletedEvent;
import com.peerpay.common.event.PaymentFailedEvent;
import com.peerpay.common.event.PaymentInitiatedEvent;
import com.peerpay.common.exception.ConcurrencyConflictException;
import com.peerpay.common.exception.InvalidPaymentStateException;
import com.peerpay.common.exception.ResourceNotFoundException;
import com.peerpay.payment.model.Payment;
import com.peerpay.payment.model.PaymentOutbox;
import com.peerpay.payment.model.PaymentStatus;
import com.peerpay.payment.repository.OutboxRepository;
import com.peerpay.payment.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public PaymentService(PaymentRepository paymentRepository,
                          OutboxRepository outboxRepository,
                          ObjectMapper objectMapper) {
        this.paymentRepository = paymentRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Initiates a payment transaction and registers an outbox event.
     */
    @Transactional
    public PaymentResponse initiatePayment(PaymentRequest request) {
        log.info("Initiating payment from {} to {} of amount ₹{}", request.getPayerUpiId(), request.getPayeeUpiId(), request.getAmount());

        String transactionId = UUID.randomUUID().toString();

        Payment payment = Payment.builder()
                .transactionId(transactionId)
                .payerId(request.getPayerId())
                .payeeId(request.getPayeeId())
                .payerUpiId(request.getPayerUpiId())
                .payeeUpiId(request.getPayeeUpiId())
                .amount(request.getAmount())
                .currency(request.getCurrency() != null ? request.getCurrency() : "INR")
                .status(PaymentStatus.INITIATED)
                .remarks(request.getRemarks())
                .initiatedAt(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);

        // Save event to Outbox table
        PaymentInitiatedEvent event = PaymentInitiatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .transactionId(transactionId)
                .payerUpiId(payment.getPayerUpiId())
                .payeeUpiId(payment.getPayeeUpiId())
                .payerId(payment.getPayerId())
                .payeeId(payment.getPayeeId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .remarks(payment.getRemarks())
                .initiatedAt(payment.getInitiatedAt())
                .eventTimestamp(LocalDateTime.now())
                .build();

        saveToOutbox("PaymentInitiated", event);

        return mapToResponse(payment);
    }

    /**
     * Completes a payment transaction (simulates UPI PSP callback).
     * Enforces State Machine rules and uses Optimistic Locking concurrency control.
     */
    @Transactional
    public PaymentResponse completePayment(String transactionId) {
        log.info("Completing payment for transaction ID: {}", transactionId);

        Payment payment = paymentRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", transactionId));

        if (payment.getStatus() != PaymentStatus.INITIATED && payment.getStatus() != PaymentStatus.PROCESSING) {
            throw new InvalidPaymentStateException(payment.getStatus().name(), PaymentStatus.COMPLETED.name());
        }

        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setUpdatedAt(LocalDateTime.now());

        try {
            paymentRepository.save(payment);
        } catch (ObjectOptimisticLockingFailureException ex) {
            log.error("Optimistic lock conflict when completing transaction: {}", transactionId);
            throw new ConcurrencyConflictException("Payment", transactionId);
        }

        // Save event to Outbox table
        PaymentCompletedEvent event = PaymentCompletedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .transactionId(transactionId)
                .payerUpiId(payment.getPayerUpiId())
                .payeeUpiId(payment.getPayeeUpiId())
                .payerId(payment.getPayerId())
                .payeeId(payment.getPayeeId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .initiatedAt(payment.getInitiatedAt())
                .completedAt(payment.getUpdatedAt())
                .eventTimestamp(LocalDateTime.now())
                .build();

        saveToOutbox("PaymentCompleted", event);

        return mapToResponse(payment);
    }

    /**
     * Fails a payment transaction.
     */
    @Transactional
    public PaymentResponse failPayment(String transactionId, String reason, String errorCode) {
        log.warn("Failing payment for transaction ID: {} | Reason: {} | Code: {}", transactionId, reason, errorCode);

        Payment payment = paymentRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", transactionId));

        if (payment.getStatus() != PaymentStatus.INITIATED && payment.getStatus() != PaymentStatus.PROCESSING) {
            throw new InvalidPaymentStateException(payment.getStatus().name(), PaymentStatus.FAILED.name());
        }

        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(reason);
        payment.setErrorCode(errorCode);
        payment.setUpdatedAt(LocalDateTime.now());

        try {
            paymentRepository.save(payment);
        } catch (ObjectOptimisticLockingFailureException ex) {
            log.error("Optimistic lock conflict when failing transaction: {}", transactionId);
            throw new ConcurrencyConflictException("Payment", transactionId);
        }

        // Save event to Outbox table
        PaymentFailedEvent event = PaymentFailedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .transactionId(transactionId)
                .payerUpiId(payment.getPayerUpiId())
                .payeeUpiId(payment.getPayeeUpiId())
                .payerId(payment.getPayerId())
                .payeeId(payment.getPayeeId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .failureReason(reason)
                .errorCode(errorCode)
                .initiatedAt(payment.getInitiatedAt())
                .failedAt(payment.getUpdatedAt())
                .eventTimestamp(LocalDateTime.now())
                .build();

        saveToOutbox("PaymentFailed", event);

        return mapToResponse(payment);
    }

    public PaymentResponse getPayment(String transactionId) {
        Payment payment = paymentRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", transactionId));
        return mapToResponse(payment);
    }

    private void saveToOutbox(String eventType, Object event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            PaymentOutbox outbox = PaymentOutbox.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType(eventType)
                    .payload(payload)
                    .processed(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            outboxRepository.save(outbox);
            log.debug("Event of type {} saved to transactional outbox", eventType);
        } catch (Exception e) {
            log.error("Failed to save event to outbox table", e);
            throw new RuntimeException("Outbox saving failed", e);
        }
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .transactionId(payment.getTransactionId())
                .payerUpiId(payment.getPayerUpiId())
                .payeeUpiId(payment.getPayeeUpiId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus().name())
                .remarks(payment.getRemarks())
                .initiatedAt(payment.getInitiatedAt())
                .completedAt(payment.getUpdatedAt())
                .build();
    }
}
