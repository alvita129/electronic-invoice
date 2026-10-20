package com.thesis.electronic_invoice.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class InvoiceRequest {
    @NotBlank
    private String invoiceNumber;
    @NotNull
    private LocalDate issuedDt;
    @NotNull
    private LocalDate dueDt;
    @NotEmpty
    @Valid
    private List<ItemRequest> items;

    @Data
    @AllArgsConstructor
    public static class ItemRequest {
        @NotBlank
        private String itemName;
        @NotNull
        private BigDecimal unitPrice;
        @NotNull
        private Integer quantity;
        private String remarks;
    }
}
