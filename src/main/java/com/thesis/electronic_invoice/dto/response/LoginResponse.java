package com.thesis.electronic_invoice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String tokenType;
    private long expiresIn;
    private UserView user;

    @Data
    @AllArgsConstructor
    public static class UserView {
        private UUID userId;
        private String username;
        private String firstName;
        private String lastName;
        private String email;
        private String userRole;
    }
}
