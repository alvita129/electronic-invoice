package com.thesis.electronic_invoice.controller;

import com.thesis.electronic_invoice.dto.request.UserRequest;
import com.thesis.electronic_invoice.dto.response.LoginResponse;
import com.thesis.electronic_invoice.dto.response.ResponseDTO;
import com.thesis.electronic_invoice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseDTO<List<LoginResponse.UserView>> list() {
        return ResponseDTO.success("Users loaded", userService.list());
    }

    @PostMapping
    public ResponseDTO<LoginResponse.UserView> create(@Valid @RequestBody UserRequest request) {
        return ResponseDTO.success("User created", userService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseDTO<LoginResponse.UserView> update(@PathVariable UUID id, @Valid @RequestBody UserRequest request) {
        return ResponseDTO.success("User updated", userService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseDTO<Void> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseDTO.success("User deleted", null);
    }
}
