package com.thesis.electronic_invoice.controller;

import com.thesis.electronic_invoice.dto.AuditView;
import com.thesis.electronic_invoice.dto.response.ResponseDTO;
import com.thesis.electronic_invoice.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit-trail")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AuditTrailController {
    private final AuditService auditService;

    @GetMapping
    public ResponseDTO<List<AuditView>> list() {
        return ResponseDTO.success("Audit trail loaded", auditService.list());
    }
}
