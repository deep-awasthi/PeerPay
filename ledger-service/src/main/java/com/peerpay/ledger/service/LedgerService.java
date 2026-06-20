package com.peerpay.ledger.service;

import com.peerpay.ledger.model.EntryType;
import com.peerpay.ledger.model.LedgerEntry;
import com.peerpay.ledger.repository.LedgerEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LedgerService {

    private static final Logger log = LoggerFactory.getLogger(LedgerService.class);

    private final LedgerEntryRepository repository;

    public LedgerService(LedgerEntryRepository repository) {
        this.repository = repository;
    }

    /**
     * Records double-entry transaction.
     * payerId gets debited, payeeId gets credited.
     */
    @Transactional
    public void recordTransaction(String transactionId, String payerId, String payeeId, BigDecimal amount, String currency) {
        log.info("Recording double-entry for transaction: {}. Amount: ₹{}", transactionId, amount);

        // Check for idempotency: has this transaction already been recorded in the ledger?
        List<LedgerEntry> existingEntries = repository.findByTransactionId(transactionId);
        if (!existingEntries.isEmpty()) {
            log.warn("Transaction: {} already processed in ledger. Skipping duplicate record.", transactionId);
            return;
        }

        // 1. Payer Debit Entry
        LedgerEntry debitEntry = LedgerEntry.builder()
                .transactionId(transactionId)
                .userId(payerId)
                .entryType(EntryType.DEBIT)
                .amount(amount)
                .currency(currency)
                .createdAt(LocalDateTime.now())
                .build();

        // 2. Payee Credit Entry
        LedgerEntry creditEntry = LedgerEntry.builder()
                .transactionId(transactionId)
                .userId(payeeId)
                .entryType(EntryType.CREDIT)
                .amount(amount)
                .currency(currency)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(debitEntry);
        repository.save(creditEntry);

        log.info("Ledger records saved for transaction: {}. Payer: {} (DEBIT), Payee: {} (CREDIT)", transactionId, payerId, payeeId);
    }

    public BigDecimal getBalance(String userId) {
        log.info("Fetching balance for user: {}", userId);
        return repository.getBalanceByUserId(userId);
    }
}
