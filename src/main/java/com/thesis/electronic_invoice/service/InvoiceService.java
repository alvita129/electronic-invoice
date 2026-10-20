package com.thesis.electronic_invoice.service;

import com.thesis.electronic_invoice.constant.UserRole;
import com.thesis.electronic_invoice.constant.ApprovalDecision;
import com.thesis.electronic_invoice.dto.AuditView;
import com.thesis.electronic_invoice.dto.InvoiceView;
import com.thesis.electronic_invoice.dto.request.InvoiceRequest;

import java.util.List;
import java.util.UUID;

public interface InvoiceService {
    List<InvoiceView> list();

    InvoiceView get(UUID id);

    InvoiceView create(InvoiceRequest request);

    InvoiceView createAndSubmit(InvoiceRequest request);

    InvoiceView update(UUID id, InvoiceRequest request);

    void delete(UUID id);

    InvoiceView submit(UUID id);

    List<InvoiceView> pending(UserRole role);

    InvoiceView decide(UUID id, ApprovalDecision decision, String remarks);

    List<AuditView> trail(UUID id);
}
