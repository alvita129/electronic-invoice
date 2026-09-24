package com.thesis.electronic_invoice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.thesis.electronic_invoice.constant.AuditAction;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.ColumnTransformer;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "AUDIT_TRAILS")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuditTrails implements Serializable {
    private static final long serialVersionUID = 783719346636305093L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "AUDIT_TRAILS_ID", updatable = false, nullable = false)
    private UUID auditTrailsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INVOICE_ID", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private MstInvoices invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private MstUsers user;

    @Column(name = "ACTION")
    @Enumerated(EnumType.STRING)
    private AuditAction action;

    @Column(name = "AUDIT_DETAILS", columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String auditDetails;

    @Column(name = "LOG_DT")
    private LocalDateTime logDt;
}
