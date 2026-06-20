package com.peerpay.reconciliation.controller;

import com.peerpay.common.dto.ApiResponse;
import com.peerpay.reconciliation.model.DlqRecord;
import com.peerpay.reconciliation.service.ReconciliationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reconciliation")
public class ReconciliationController {

    private final ReconciliationService reconciliationService;

    public ReconciliationController(ReconciliationService reconciliationService) {
        this.reconciliationService = reconciliationService;
    }

    /**
     * Checks if a transaction state matches ledger double-entry bookkeeping details.
     */
    @GetMapping("/validate/{transactionId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateTransaction(@PathVariable String transactionId) {
        Map<String, Object> report = reconciliationService.reconcileTransaction(transactionId);
        return ResponseEntity.ok(ApiResponse.success(report, "Reconciliation check completed"));
    }

    /**
     * Fetches all unresolved DLQ records.
     */
    @GetMapping("/dlq")
    public ResponseEntity<ApiResponse<List<DlqRecord>>> getDlqRecords() {
        List<DlqRecord> records = reconciliationService.getUnresolvedDlqs();
        return ResponseEntity.ok(ApiResponse.success(records, "Unresolved DLQ records fetched"));
    }

    /**
     * Marks a DLQ record as resolved after manual check.
     */
    @PostMapping("/dlq/{id}/resolve")
    public ResponseEntity<ApiResponse<Void>> resolveDlq(@PathVariable Long id) {
        reconciliationService.resolveDlq(id);
        return ResponseEntity.ok(ApiResponse.success(null, "DLQ record marked as resolved"));
    }
}
