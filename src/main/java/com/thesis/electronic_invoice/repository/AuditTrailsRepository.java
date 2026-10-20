package com.thesis.electronic_invoice.repository;

import com.thesis.electronic_invoice.model.AuditTrails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuditTrailsRepository extends JpaRepository<AuditTrails, UUID> {
    List<AuditTrails> findByInvoiceInvoiceIdOrderByLogDtDesc(UUID invoiceId);
    List<AuditTrails> findAllByOrderByLogDtDesc();
}
