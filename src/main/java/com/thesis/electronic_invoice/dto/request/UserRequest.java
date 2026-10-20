package com.thesis.electronic_invoice.dto.request;

import com.thesis.electronic_invoice.constant.UserRole;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserRequest {
    @NotBlank
    private String username;
    private String password;
    @NotNull
    private UserRole userRole;
    @NotBlank
    private String firstName;
    private String lastName;
    private String email;
}
