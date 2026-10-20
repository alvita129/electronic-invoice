package com.thesis.electronic_invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class AuditView {
    private UUID auditTrailsId;
    private UUID invoiceId;
    private String invoiceNumber;
    private UUID userId;
    private String username;
    private String userRole;
    private String action;
    private String auditDetails;
    private LocalDateTime logDt;
}
