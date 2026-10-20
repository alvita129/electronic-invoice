package com.thesis.electronic_invoice.service;

import com.thesis.electronic_invoice.constant.AuditAction;
import com.thesis.electronic_invoice.model.MstInvoices;
import com.thesis.electronic_invoice.model.MstUsers;
import com.thesis.electronic_invoice.dto.AuditView;

import java.util.List;

public interface AuditService {
    void record(MstInvoices invoice, MstUsers actor, AuditAction action, Object before, Object after, String remarks);
    void recordAuthentication(MstUsers actor, AuditAction action);
    List<AuditView> list();
}
