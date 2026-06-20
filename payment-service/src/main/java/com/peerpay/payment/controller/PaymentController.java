package com.peerpay.payment.controller;

import com.peerpay.common.dto.ApiResponse;
import com.peerpay.common.dto.PaymentRequest;
import com.peerpay.common.dto.PaymentResponse;
import com.peerpay.payment.service.IdempotencyService;
import com.peerpay.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;
    private final IdempotencyService idempotencyService;

    public PaymentController(PaymentService paymentService, IdempotencyService idempotencyService) {
        this.paymentService = paymentService;
        this.idempotencyService = idempotencyService;
    }

    /**
     * Initiates a payment. Checks and enforces idempotency.
     */
    @PostMapping("/initiate")
    public ResponseEntity<ApiResponse<PaymentResponse>> initiatePayment(
            @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody PaymentRequest request) {

        log.info("Initiating payment request. Idempotency Key: {}", idempotencyKey);

        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Optional<PaymentResponse> cachedResponse = idempotencyService.getResponse(idempotencyKey);
            if (cachedResponse.isPresent()) {
                log.info("Returning cached response for idempotency key: {}", idempotencyKey);
                return ResponseEntity.ok(ApiResponse.success(cachedResponse.get(), "Duplicate request. Retrieved from cache."));
            }
        }

        // Process request
        PaymentResponse response = paymentService.initiatePayment(request);

        // Store response for idempotency
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            idempotencyService.saveResponse(idempotencyKey, response);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Payment initiated successfully"));
    }

    /**
     * Simulates external UPI gateway/bank completion webhook.
     */
    @PostMapping("/{transactionId}/complete")
    public ResponseEntity<ApiResponse<PaymentResponse>> completePayment(@PathVariable String transactionId) {
        log.info("Received simulation request to complete transaction: {}", transactionId);
        PaymentResponse response = paymentService.completePayment(transactionId);
        return ResponseEntity.ok(ApiResponse.success(response, "Payment completed successfully"));
    }

    /**
     * Simulates external UPI gateway/bank failure webhook.
     */
    @PostMapping("/{transactionId}/fail")
    public ResponseEntity<ApiResponse<PaymentResponse>> failPayment(
            @PathVariable String transactionId,
            @RequestParam(defaultValue = "Simulator payment failure") String reason,
            @RequestParam(defaultValue = "PSP_REJECTED") String errorCode) {
        log.info("Received simulation request to fail transaction: {}", transactionId);
        PaymentResponse response = paymentService.failPayment(transactionId, reason, errorCode);
        return ResponseEntity.ok(ApiResponse.success(response, "Payment marked as failed"));
    }

    /**
     * Fetches details of a payment transaction.
     */
    @GetMapping("/{transactionId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(@PathVariable String transactionId) {
        PaymentResponse response = paymentService.getPayment(transactionId);
        return ResponseEntity.ok(ApiResponse.success(response, "Payment details fetched"));
    }
}
