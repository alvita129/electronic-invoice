package com.thesis.electronic_invoice.model;

import com.thesis.electronic_invoice.constant.ApprovalStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.UUID;

@Entity
@Table(name = "APPROVAL_WORKFLOWS")
@Data
public class ApprovalWorkflows extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "APPROVAL_WORKFLOW_ID", updatable = false, nullable = false)
    private UUID approvalWorkflowId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INVOICE_ID", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private MstInvoices invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private MstUsers approver;

    @Column(name = "LEVEL", nullable = false)
    private Integer level;

    @Column(name = "REMARKS")
    private String remarks;

    @Column(name = "APPROVAL_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;
}
