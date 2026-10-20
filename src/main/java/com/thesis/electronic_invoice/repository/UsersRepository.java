package com.thesis.electronic_invoice.repository;

import com.thesis.electronic_invoice.model.MstUsers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

import com.thesis.electronic_invoice.constant.UserRole;

import java.util.List;

public interface UsersRepository extends JpaRepository<MstUsers, UUID> {
    Optional<MstUsers> findByUsernameAndActiveTrue(String username);

    boolean existsByUsernameAndActiveTrue(String username);

    List<MstUsers> findAllByActiveTrueOrderByUsernameAsc();

    List<MstUsers> findByUserRole(UserRole userRole);
    List<MstUsers> findByUserRoleAndActiveTrue(UserRole userRole);
}
