package com.thesis.electronic_invoice.repository;

import com.thesis.electronic_invoice.constant.ApprovalStatus;
import com.thesis.electronic_invoice.model.ApprovalWorkflows;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.thesis.electronic_invoice.constant.UserRole;

public interface ApprovalWorkflowsRepository extends JpaRepository<ApprovalWorkflows, UUID> {
    List<ApprovalWorkflows> findByInvoiceInvoiceIdOrderByLevelAscCreatedDtAsc(UUID invoiceId);

    List<ApprovalWorkflows> findByApproverUsernameAndApprovalStatusOrderByCreatedDtAsc(String username, ApprovalStatus status);

    Optional<ApprovalWorkflows> findFirstByInvoiceInvoiceIdAndLevelAndApprovalStatus(UUID invoiceId, Integer level, ApprovalStatus status);

    List<ApprovalWorkflows> findByApproverUserRoleAndApprovalStatusOrderByCreatedDtAsc(UserRole role, ApprovalStatus status);

    boolean existsByApproverUserIdAndApprovalStatus(UUID userId, ApprovalStatus status);
}
