package com.thesis.electronic_invoice.repository;

import com.thesis.electronic_invoice.model.InvoiceItems;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InvoiceItemsRepository extends JpaRepository<InvoiceItems, UUID> {
    List<InvoiceItems> findByInvoiceInvoiceId(UUID invoiceId);

    void deleteByInvoiceInvoiceId(UUID invoiceId);
}
