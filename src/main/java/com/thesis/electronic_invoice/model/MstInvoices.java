package com.thesis.electronic_invoice.model;

import com.thesis.electronic_invoice.constant.InvoiceStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private MstUsers admin;

    @Column(name = "ISSUED_DT", nullable = false)
    private LocalDate issuedDt;

    @Column(name = "INVOICE_NUMBER", nullable = false, unique = true)
    private String invoiceNumber;

    @Column(name = "INVOICE_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private InvoiceStatus invoiceStatus;

    @Column(name = "DUE_DT", nullable = false)
    private LocalDate dueDt;

    @Column(name = "TOTAL_AMOUNT", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "ARCHIVED", nullable = false)
    private boolean archived;
}
