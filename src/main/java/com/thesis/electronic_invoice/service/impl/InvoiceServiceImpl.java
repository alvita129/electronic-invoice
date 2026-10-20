package com.thesis.electronic_invoice.service.impl;

import com.thesis.electronic_invoice.constant.*;
import com.thesis.electronic_invoice.dto.*;
import com.thesis.electronic_invoice.dto.request.InvoiceRequest;
import com.thesis.electronic_invoice.model.*;
import com.thesis.electronic_invoice.repository.*;
import com.thesis.electronic_invoice.repository.UsersRepository;
import com.thesis.electronic_invoice.service.AuditService;
import com.thesis.electronic_invoice.service.CurrentUserService;
import com.thesis.electronic_invoice.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoicesRepository invoices;
    private final InvoiceItemsRepository items;
    private final ApprovalWorkflowsRepository approvals;
    private final UsersRepository users;
    private final AuditService audit;
    private final CurrentUserService current;

    @Transactional(readOnly = true)
    public List<InvoiceView> list() {
        return invoices.findAllByOrderByCreatedDtDesc().stream().map(this::view).toList();
    }

    @Transactional(readOnly = true)
    public InvoiceView get(UUID id) {
        return view(find(id));
    }

    @Transactional
    public InvoiceView create(InvoiceRequest request) {
        return createInvoice(request, true);
    }

    @Transactional
    public InvoiceView createAndSubmit(InvoiceRequest request) {
        InvoiceView draft = createInvoice(request, false);
        return submitInvoice(draft.getInvoiceId(), current.get());
    }

    private InvoiceView createInvoice(InvoiceRequest request, boolean recordCreate) {
        validateDates(request);
        if (invoices.existsByInvoiceNumberIgnoreCase(request.getInvoiceNumber()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Invoice number already exists");
        MstUsers actor = current.get();
        MstInvoices invoice = new MstInvoices();
        invoice.setAdmin(actor);
        invoice.setInvoiceNumber(request.getInvoiceNumber().trim());
        invoice.setIssuedDt(request.getIssuedDt());
        invoice.setDueDt(request.getDueDt());
        invoice.setInvoiceStatus(InvoiceStatus.DRAFT);
        stampNew(invoice, actor);
        invoice.setTotalAmount(total(request.getItems()));
        invoices.save(invoice);
        replaceItems(invoice, request.getItems(), actor);
        if (recordCreate) audit.record(invoice, actor, AuditAction.CREATE_INVOICE, null, view(invoice), null);
        return view(invoice);
    }

    @Transactional
    public InvoiceView update(UUID id, InvoiceRequest request) {
        validateDates(request);
        MstUsers actor = current.get();
        MstInvoices invoice = find(id);
        if (!invoice.isArchived() && invoice.getInvoiceStatus() != InvoiceStatus.DRAFT && invoice.getInvoiceStatus() != InvoiceStatus.RETURNED_TO_ADMIN)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only draft or returned invoices can be edited");
        if (!invoice.getInvoiceNumber().equalsIgnoreCase(request.getInvoiceNumber().trim()) && invoices.existsByInvoiceNumberIgnoreCase(request.getInvoiceNumber()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Invoice number already exists");
        InvoiceView before = view(invoice);
        invoice.setInvoiceNumber(request.getInvoiceNumber().trim());
        invoice.setIssuedDt(request.getIssuedDt());
        invoice.setDueDt(request.getDueDt());
        invoice.setTotalAmount(total(request.getItems()));
        invoice.setInvoiceStatus(InvoiceStatus.DRAFT);
        stampUpdate(invoice, actor);
        replaceItems(invoice, request.getItems(), actor);
        invoices.save(invoice);
        audit.record(invoice, actor, AuditAction.UPDATE_INVOICE, before, view(invoice), null);
        return view(invoice);
    }

    @Transactional
    public void delete(UUID id) {
        MstUsers actor = current.get();
        MstInvoices invoice = find(id);
        if (invoice.isArchived())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Invoice is already archived");
        if (invoice.getInvoiceStatus() != InvoiceStatus.DRAFT)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only draft invoices can be archived");
        InvoiceView before = view(invoice);
        invoice.setArchived(true);
        stampUpdate(invoice, actor);
        invoices.save(invoice);
        audit.record(invoice, actor, AuditAction.DELETE_INVOICE, before, view(invoice), null);
    }

    @Transactional
    public InvoiceView submit(UUID id) {
        MstUsers actor = current.get();
        return submitInvoice(id, actor);
    }

    private InvoiceView submitInvoice(UUID id, MstUsers actor) {
        MstInvoices invoice = find(id);
        if (invoice.getInvoiceStatus() != InvoiceStatus.DRAFT && invoice.getInvoiceStatus() != InvoiceStatus.RETURNED_TO_ADMIN)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Invoice is not ready to submit");
        if (items.findByInvoiceInvoiceId(id).isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invoice requires at least one item");
        MstUsers l1 = users.findByUserRoleAndActiveTrue(UserRole.APPROVER_LV1).stream().findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Create an active Approver Level 1 user before submitting"));
        MstUsers l2 = users.findByUserRoleAndActiveTrue(UserRole.APPROVER_LV2).stream().findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Create an active Approver Level 2 user before submitting"));
        InvoiceView before = view(invoice);
        invoice.setArchived(false);
        approvals.findByInvoiceInvoiceIdOrderByLevelAscCreatedDtAsc(id).stream().filter(a -> a.getApprovalStatus() == ApprovalStatus.PENDING).forEach(a -> {
            a.setApprovalStatus(ApprovalStatus.RETURN_TO_ADMIN);
            a.setUpdatedDt(LocalDateTime.now());
            a.setUpdatedBy(actor.getUsername());
        });
        createApproval(invoice, l1, 1, actor);
        createApproval(invoice, l2, 2, actor);
        invoice.setInvoiceStatus(InvoiceStatus.SUBMITTED);
        stampUpdate(invoice, actor);
        invoices.save(invoice);
        audit.record(invoice, actor, AuditAction.SUBMIT, before, view(invoice), null);
        return view(invoice);
    }

    @Transactional(readOnly = true)
    public List<InvoiceView> pending(UserRole role) {
        return approvals.findByApproverUserRoleAndApprovalStatusOrderByCreatedDtAsc(role, ApprovalStatus.PENDING).stream().filter(a -> (a.getLevel() == 1 && a.getInvoice().getInvoiceStatus() == InvoiceStatus.SUBMITTED) || (a.getLevel() == 2 && a.getInvoice().getInvoiceStatus() == InvoiceStatus.APPROVED_LV1)).map(a -> view(a.getInvoice())).distinct().toList();
    }

    @Transactional
    public InvoiceView decide(UUID id, ApprovalDecision decision, String remarks) {
        MstUsers actor = current.get();
        MstInvoices invoice = find(id);
        Integer level = actor.getUserRole().getApprovalLevel();
        if (level == 0) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Approver role required");
        InvoiceStatus expected = level == 1 ? InvoiceStatus.SUBMITTED : InvoiceStatus.APPROVED_LV1;
        if (invoice.getInvoiceStatus() != expected)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Invoice is not awaiting your approval level");
        ApprovalWorkflows step = approvals.findFirstByInvoiceInvoiceIdAndLevelAndApprovalStatus(id, level, ApprovalStatus.PENDING).orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "No pending approval step"));
        if (!step.getApprover().getUserId().equals(actor.getUserId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invoice is assigned to another approver");
        if (decision != ApprovalDecision.APPROVE && (remarks == null || remarks.isBlank()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A reason is required for reject or return");
        InvoiceView before = view(invoice);
        step.setApprover(actor);
        step.setRemarks(remarks);
        step.setUpdatedBy(actor.getUsername());
        step.setUpdatedDt(LocalDateTime.now());
        AuditAction action;
        if (decision == ApprovalDecision.APPROVE) {
            step.setApprovalStatus(level == 1 ? ApprovalStatus.APPROVAL_LV1 : ApprovalStatus.APPROVAL_LV2);
            invoice.setInvoiceStatus(level == 1 ? InvoiceStatus.APPROVED_LV1 : InvoiceStatus.APPROVED_LV2);
            action = level == 1 ? AuditAction.APPROVE_LV1 : AuditAction.APPROVE_LV2;
        } else if (decision == ApprovalDecision.REJECT) {
            step.setApprovalStatus(ApprovalStatus.REJECTED);
            invoice.setInvoiceStatus(InvoiceStatus.REJECTED);
            action = AuditAction.REJECT;
        } else {
            step.setApprovalStatus(ApprovalStatus.RETURN_TO_ADMIN);
            invoice.setInvoiceStatus(InvoiceStatus.RETURNED_TO_ADMIN);
            action = AuditAction.RETURN_TO_ADMIN;
        }
        stampUpdate(invoice, actor);
        if (decision != ApprovalDecision.APPROVE) {
            ApprovalStatus closingStatus = decision == ApprovalDecision.REJECT ? ApprovalStatus.REJECTED : ApprovalStatus.RETURN_TO_ADMIN;
            approvals.findByInvoiceInvoiceIdOrderByLevelAscCreatedDtAsc(id).stream()
                    .filter(other -> !other.getApprovalWorkflowId().equals(step.getApprovalWorkflowId())
                            && other.getApprovalStatus() == ApprovalStatus.PENDING)
                    .forEach(other -> {
                        other.setApprovalStatus(closingStatus);
                        other.setRemarks(remarks);
                        other.setUpdatedBy(actor.getUsername());
                        other.setUpdatedDt(LocalDateTime.now());
                    });
        }
        approvals.save(step);
        invoices.save(invoice);
        audit.record(invoice, actor, action, before, view(invoice), remarks);
        return view(invoice);
    }

    @Transactional(readOnly = true)
    public List<AuditView> trail(UUID id) {
        find(id);
        return auditRepository.findByInvoiceInvoiceIdOrderByLogDtDesc(id).stream().map(a -> new AuditView(a.getAuditTrailsId(), a.getInvoice().getInvoiceId(), a.getInvoice().getInvoiceNumber(), a.getUser().getUserId(), a.getUser().getUsername(), a.getUser().getUserRole().name(), a.getAction().name(), a.getAuditDetails(), a.getLogDt())).toList();
    }

    private final AuditTrailsRepository auditRepository;

    private MstInvoices find(UUID id) {
        return invoices.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));
    }

    private InvoiceView view(MstInvoices invoice) {
        List<InvoiceView.ItemView> lines = items.findByInvoiceInvoiceId(invoice.getInvoiceId()).stream().map(x -> new InvoiceView.ItemView(x.getInvoiceItemsId(), x.getItemName(), x.getUnitPrice(), x.getQuantity(), x.getTotalPrice(), x.getRemarks())).toList();
        return new InvoiceView(invoice.getInvoiceId(), invoice.getInvoiceNumber(), invoice.getIssuedDt(), invoice.getDueDt(), invoice.getTotalAmount(), invoice.getInvoiceStatus(), invoice.isArchived(), invoice.getCreatedBy(), invoice.getCreatedDt(), lines);
    }

    private void replaceItems(MstInvoices invoice, List<InvoiceRequest.ItemRequest> request, MstUsers actor) {
        items.deleteByInvoiceInvoiceId(invoice.getInvoiceId());
        for (InvoiceRequest.ItemRequest row : request) {
            InvoiceItems item = new InvoiceItems();
            item.setInvoice(invoice);
            item.setItemName(row.getItemName().trim());
            item.setUnitPrice(row.getUnitPrice());
            item.setQuantity(row.getQuantity());
            item.setTotalPrice(row.getUnitPrice().multiply(BigDecimal.valueOf(row.getQuantity())));
            item.setRemarks(row.getRemarks());
            stampNew(item, actor);
            items.save(item);
        }
    }

    private BigDecimal total(List<InvoiceRequest.ItemRequest> rows) {
        return rows.stream().map(r -> r.getUnitPrice().multiply(BigDecimal.valueOf(r.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validateDates(InvoiceRequest request) {
        if (request.getDueDt().isBefore(request.getIssuedDt()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Due date cannot be before issue date");
    }

    private void createApproval(MstInvoices invoice, MstUsers user, Integer level, MstUsers actor) {
        ApprovalWorkflows approval = new ApprovalWorkflows();
        approval.setInvoice(invoice);
        approval.setApprover(user);
        approval.setLevel(level);
        approval.setApprovalStatus(ApprovalStatus.PENDING);
        stampNew(approval, actor);
        approvals.save(approval);
    }

    private void stampNew(BaseModel baseModel, MstUsers actor) {
        baseModel.setCreatedBy(actor.getUsername());
        baseModel.setCreatedDt(LocalDateTime.now());
    }

    private void stampUpdate(BaseModel baseModel, MstUsers actor) {
        baseModel.setUpdatedBy(actor.getUsername());
        baseModel.setUpdatedDt(LocalDateTime.now());
    }
}
