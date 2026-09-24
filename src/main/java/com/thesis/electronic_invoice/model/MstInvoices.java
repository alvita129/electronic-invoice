package com.thesis.electronic_invoice.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "MST_INVOICES")
@Data
public class MstInvoices extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "INVOICE_ID", updatable = false, nullable = false)
    private UUID invoiceId;

    @Column(name = "ISSUED_DT", nullable = false)
    private LocalDate issuedDt;

    @Column(name = "INVOICE_NUMBER", nullable = false, unique = true)
    private String invoiceNumber;

    @Column(name = "DUE_DT", nullable = false)
    private LocalDate dueDt;

    @Column(name = "TOTAL_AMOUNT", nullable = false)
    private BigDecimal totalAmount;
}
