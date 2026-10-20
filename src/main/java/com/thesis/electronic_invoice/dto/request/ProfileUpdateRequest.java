package com.thesis.electronic_invoice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProfileUpdateRequest {
    @NotBlank
    private String firstName;
    private String lastName;
    @Email
    private String email;
    private String password;
}
