package com.thesis.electronic_invoice.model;

import com.thesis.electronic_invoice.constant.UserRole;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "MST_USERS")
@Data
public class MstUsers extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "USER_ID", updatable = false, nullable = false)
    private UUID userId;

    @Column(name = "USERNAME", nullable = false, unique = true)
    private String username;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "USER_ROLE", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Column(name = "FIRST_NAME", nullable = false)
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "EMAIL")
    private String email;
}
