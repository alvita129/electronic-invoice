package com.thesis.electronic_invoice.controller;

import com.thesis.electronic_invoice.dto.*;
import com.thesis.electronic_invoice.dto.request.InvoiceRequest;
import com.thesis.electronic_invoice.dto.response.ResponseDTO;
import com.thesis.electronic_invoice.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class InvoiceController {
    private final InvoiceService service;

    @GetMapping
    public ResponseDTO<List<InvoiceView>> list() {
        return ResponseDTO.success("Invoices loaded", service.list());
    }

    @GetMapping("/{id}")
    public ResponseDTO<InvoiceView> get(@PathVariable UUID id) {
        return ResponseDTO.success("Invoice loaded", service.get(id));
    }

    @PostMapping
    public ResponseDTO<InvoiceView> create(@Valid @RequestBody InvoiceRequest request) {
        return ResponseDTO.success("Invoice created", service.create(request));
    }

    @PostMapping("/submit")
    public ResponseDTO<InvoiceView> createAndSubmit(@Valid @RequestBody InvoiceRequest request) {
        return ResponseDTO.success("Invoice submitted for approval", service.createAndSubmit(request));
    }

    @PutMapping("/{id}")
    public ResponseDTO<InvoiceView> update(@PathVariable UUID id, @Valid @RequestBody InvoiceRequest request) {
        return ResponseDTO.success("Invoice updated", service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseDTO<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseDTO.success("Draft archived", null);
    }

    @PostMapping("/{id}/submit")
    public ResponseDTO<InvoiceView> submit(@PathVariable UUID id) {
        return ResponseDTO.success("Invoice submitted for approval", service.submit(id));
    }

    @GetMapping("/{id}/trail")
    public ResponseDTO<List<AuditView>> audit(@PathVariable UUID id) {
        return ResponseDTO.success("Invoice trail loaded", service.trail(id));
    }
}
