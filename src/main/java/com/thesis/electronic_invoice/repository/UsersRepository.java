package com.thesis.electronic_invoice.repository;

import com.thesis.electronic_invoice.model.MstUsers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsersRepository extends JpaRepository<MstUsers, UUID> {
    Optional<MstUsers> findByUsername(String username);
    boolean existsByUsername(String username);
}
