package com.peerpay.reconciliation.repository;

import com.peerpay.reconciliation.model.DlqRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DlqRecordRepository extends JpaRepository<DlqRecord, Long> {
    List<DlqRecord> findByResolvedFalse();
}
