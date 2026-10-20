package com.thesis.electronic_invoice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thesis.electronic_invoice.constant.AuditAction;
import com.thesis.electronic_invoice.model.AuditTrails;
import com.thesis.electronic_invoice.model.MstInvoices;
import com.thesis.electronic_invoice.model.MstUsers;
import com.thesis.electronic_invoice.dto.AuditView;
import com.thesis.electronic_invoice.repository.AuditTrailsRepository;
import com.thesis.electronic_invoice.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.LinkedHashMap;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {
    private final AuditTrailsRepository repository;
    private final ObjectMapper mapper;

    @Override
    public void record(MstInvoices invoice, MstUsers actor, AuditAction action, Object before, Object after, String remarks) {
        try {
            LinkedHashMap<String, Object> details = new LinkedHashMap<>();
            details.put("actor", new LinkedHashMap<>(java.util.Map.of("userId", actor.getUserId(), "username", actor.getUsername(), "role", actor.getUserRole())));
            details.put("before", before);
            details.put("after", after);
            details.put("remarks", remarks);

            AuditTrails audit = new AuditTrails();
            audit.setInvoice(invoice);
            audit.setUser(actor);
            audit.setAction(action);
            audit.setAuditDetails(mapper.writeValueAsString(details));
            audit.setLogDt(LocalDateTime.now());
            repository.save(audit);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not serialize audit details", e);
        }
    }

    @Override
    public void recordAuthentication(MstUsers actor, AuditAction action) {
        AuditTrails audit = new AuditTrails();
        audit.setUser(actor);
        audit.setAction(action);
        audit.setLogDt(LocalDateTime.now());
        try {
            audit.setAuditDetails(mapper.writeValueAsString(java.util.Map.of("event", action.name())));
            repository.save(audit);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not serialize authentication audit details", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditView> list() {
        return repository.findAllByOrderByLogDtDesc().stream().map(this::view).toList();
    }

    private AuditView view(AuditTrails audit) {
        return new AuditView(audit.getAuditTrailsId(),
                audit.getInvoice() == null ? null : audit.getInvoice().getInvoiceId(),
                audit.getInvoice() == null ? null : audit.getInvoice().getInvoiceNumber(),
                audit.getUser().getUserId(), audit.getUser().getUsername(), audit.getUser().getUserRole().name(),
                audit.getAction().name(), audit.getAuditDetails(), audit.getLogDt());
    }
}
