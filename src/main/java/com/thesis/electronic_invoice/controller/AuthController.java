package com.thesis.electronic_invoice.controller;

import com.thesis.electronic_invoice.dto.request.LoginRequest;
import com.thesis.electronic_invoice.dto.request.ChangePasswordRequest;
import com.thesis.electronic_invoice.dto.request.ProfileUpdateRequest;
import com.thesis.electronic_invoice.dto.response.LoginResponse;
import com.thesis.electronic_invoice.dto.response.ResponseDTO;
import com.thesis.electronic_invoice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseDTO<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseDTO.success("Signed in successfully", authService.login(request));
    }

    @GetMapping("/profile")
    public ResponseDTO<LoginResponse.UserView> profile() {
        return ResponseDTO.success("Profile loaded", authService.profile());
    }

    @PostMapping("/logout")
    public ResponseDTO<Void> logout() {
        authService.logout();
        return ResponseDTO.success("Signed out successfully", null);
    }

    @PutMapping("/profile")
    public ResponseDTO<LoginResponse.UserView> updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        return ResponseDTO.success("Profile updated", authService.updateProfile(request));
    }

    @PutMapping("/password")
    public ResponseDTO<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseDTO.success("Password changed successfully", null);
    }
}
