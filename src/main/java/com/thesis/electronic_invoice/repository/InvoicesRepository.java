package com.thesis.electronic_invoice.repository;

import com.thesis.electronic_invoice.constant.InvoiceStatus;
import com.thesis.electronic_invoice.model.MstInvoices;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoicesRepository extends JpaRepository<MstInvoices, UUID> {
    List<MstInvoices> findAllByOrderByCreatedDtDesc();

    Optional<MstInvoices> findByInvoiceIdAndArchivedFalse(UUID id);

    long countByArchivedFalseAndInvoiceStatus(InvoiceStatus status);

    boolean existsByInvoiceNumberIgnoreCase(String invoiceNumber);
}
