package com.thesis.electronic_invoice.controller;

import com.thesis.electronic_invoice.dto.*;
import com.thesis.electronic_invoice.constant.ApprovalDecision;
import com.thesis.electronic_invoice.dto.request.DecisionRequest;
import com.thesis.electronic_invoice.dto.response.ResponseDTO;
import com.thesis.electronic_invoice.service.CurrentUserService;
import com.thesis.electronic_invoice.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/approvals")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('APPROVER_LV1','APPROVER_LV2')")
public class ApprovalController {
    private final InvoiceService invoices;
    private final CurrentUserService current;

    @GetMapping("/pending")
    public ResponseDTO<List<InvoiceView>> pending() {
        return ResponseDTO.success("Pending approvals loaded", invoices.pending(current.get().getUserRole()));
    }

    @PutMapping("/{id}/approve")
    public ResponseDTO<InvoiceView> approve(@PathVariable UUID id, @RequestBody(required = false) DecisionRequest request) {
        return ResponseDTO.success("Invoice approved", invoices.decide(id, ApprovalDecision.APPROVE, request == null ? null : request.getRemarks()));
    }

    @PutMapping("/{id}/reject")
    public ResponseDTO<InvoiceView> reject(@PathVariable UUID id, @RequestBody DecisionRequest request) {
        return ResponseDTO.success("Invoice rejected", invoices.decide(id, ApprovalDecision.REJECT, request.getRemarks()));
    }

    @PutMapping("/{id}/return")
    public ResponseDTO<InvoiceView> returnForRevision(@PathVariable UUID id, @RequestBody DecisionRequest request) {
        return ResponseDTO.success("Invoice returned for revision", invoices.decide(id, ApprovalDecision.RETURN, request.getRemarks()));
    }
}
