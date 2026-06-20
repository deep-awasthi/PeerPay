package com.peerpay.ledger.controller;

import com.peerpay.common.dto.ApiResponse;
import com.peerpay.ledger.service.LedgerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ledger")
public class LedgerController {

    private static final Logger log = LoggerFactory.getLogger(LedgerController.class);

    private final LedgerService ledgerService;

    public LedgerController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    /**
     * Checks current balance of a user (Sum of Credits - Sum of Debits).
     */
    @GetMapping("/balance/{userId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBalance(@PathVariable String userId) {
        log.info("API request to fetch balance for user: {}", userId);
        BigDecimal balance = ledgerService.getBalance(userId);
        return ResponseEntity.ok(ApiResponse.success(
                Map.of("userId", userId, "balance", balance, "currency", "INR"),
                "Balance fetched successfully"
        ));
    }
}
