package com.thesis.electronic_invoice.dto;

import com.thesis.electronic_invoice.constant.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class InvoiceView {
    private UUID invoiceId;
    private String invoiceNumber;
    private LocalDate issuedDt;
    private LocalDate dueDt;
    private BigDecimal totalAmount;
    private InvoiceStatus invoiceStatus;
    private boolean archived;
    private String createdBy;
    private LocalDateTime createdDt;
    private List<ItemView> items;

    @Data
    @AllArgsConstructor
    public static class ItemView {
        private UUID invoiceItemsId;
        private String itemName;
        private BigDecimal unitPrice;
        private Integer quantity;
        private BigDecimal totalPrice;
        private String remarks;
    }
}
