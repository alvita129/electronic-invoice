package com.thesis.electronic_invoice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "TRX_INVOICE_ITEMS")
@Data
public class TrxInvoiceItems extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "INVOICE_ITEMS_ID", updatable = false, nullable = false)
    private UUID invoiceItemsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INVOICE_ID", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private TrxInvoices invoice;

    @Column(name = "ITEM_NAME")
    private String itemName;

    @Column(name = "UNIT_PRICE", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "TOTAL_PRICE", nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "QUANTITY", nullable = false)
    private Integer quantity;

    @Column(name = "REMARKS")
    private String remarks;
}
