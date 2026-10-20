package com.thesis.electronic_invoice.service.impl;

import com.thesis.electronic_invoice.constant.ApprovalStatus;
import com.thesis.electronic_invoice.constant.AuditAction;
import com.thesis.electronic_invoice.dto.request.UserRequest;
import com.thesis.electronic_invoice.dto.response.LoginResponse;
import com.thesis.electronic_invoice.model.MstUsers;
import com.thesis.electronic_invoice.repository.ApprovalWorkflowsRepository;
import com.thesis.electronic_invoice.repository.UsersRepository;
import com.thesis.electronic_invoice.service.CurrentUserService;
import com.thesis.electronic_invoice.service.AuditService;
import com.thesis.electronic_invoice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UsersRepository users;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUser;
    private final ApprovalWorkflowsRepository approvals;
    private final AuditService audit;

    @Override
    @Transactional(readOnly = true)
    public List<LoginResponse.UserView> list() {
        return users.findAllByActiveTrueOrderByUsernameAsc().stream().map(this::view).toList();
    }

    @Override
    @Transactional
    public LoginResponse.UserView create(UserRequest request) {
        if (request.getPassword() == null || request.getPassword().length() < 8)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 8 characters");
        if (users.existsByUsernameAndActiveTrue(request.getUsername()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        MstUsers user = new MstUsers();
        copy(user, request, true);
        user.setCreatedBy(currentUser.get().getUsername());
        user.setCreatedDt(LocalDateTime.now());
        MstUsers saved = users.save(user);
        audit.record(null, currentUser.get(), AuditAction.CREATE_USER, null, snapshot(saved), null);
        return view(saved);
    }

    @Override
    @Transactional
    public LoginResponse.UserView update(UUID id, UserRequest request) {
        MstUsers user = users.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        MstUsers actor = currentUser.get();
        Map<String, Object> before = snapshot(user);
        if (!actor.getUserId().equals(id)
                && (!java.util.Objects.equals(user.getUsername(), request.getUsername())
                || (request.getPassword() != null && !request.getPassword().isBlank())
                || !java.util.Objects.equals(user.getFirstName(), request.getFirstName())
                || !java.util.Objects.equals(user.getLastName(), request.getLastName())
                || !java.util.Objects.equals(user.getEmail(), request.getEmail())))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can only change another member's role. Members manage their own profile and password.");
        if (actor.getUserId().equals(id) && (!java.util.Objects.equals(user.getUsername(), request.getUsername()) || user.getUserRole() != request.getUserRole()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Use profile settings to update your own details; username and role are fixed");
        if (user.getUserRole() != request.getUserRole()
                && approvals.existsByApproverUserIdAndApprovalStatus(id, ApprovalStatus.PENDING))
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This user has pending approval work; finish or return those invoices before changing roles");
        if (!user.getUsername().equals(request.getUsername()) && users.existsByUsernameAndActiveTrue(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        copy(user, request, request.getPassword() != null && !request.getPassword().isBlank());
        user.setUpdatedBy(actor.getUsername());
        user.setUpdatedDt(LocalDateTime.now());
        MstUsers saved = users.save(user);
        audit.record(null, actor, AuditAction.UPDATE_USER, before, snapshot(saved), null);
        return view(saved);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        MstUsers actor = currentUser.get();
        if (actor.getUserId().equals(id))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You cannot delete your own account");
        MstUsers user = users.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Map<String, Object> before = snapshot(user);
        if (approvals.existsByApproverUserIdAndApprovalStatus(id, ApprovalStatus.PENDING))
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This user has pending approval work; finish or return those invoices before deleting the account");
        user.setActive(false);
        user.setUpdatedBy(actor.getUsername());
        user.setUpdatedDt(LocalDateTime.now());
        MstUsers saved = users.save(user);
        audit.record(null, actor, AuditAction.DELETE_USER, before, snapshot(saved), null);
    }

    private Map<String, Object> snapshot(MstUsers user) {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("userId", user.getUserId());
        values.put("username", user.getUsername());
        values.put("firstName", user.getFirstName());
        values.put("lastName", user.getLastName());
        values.put("email", user.getEmail());
        values.put("role", user.getUserRole().name());
        values.put("active", user.isActive());
        return values;
    }

    private void copy(MstUsers user, UserRequest request, boolean encodePassword) {
        user.setUsername(request.getUsername().trim());
        if (encodePassword) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setUserRole(request.getUserRole());
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
    }

    private LoginResponse.UserView view(MstUsers user) {
        return new LoginResponse.UserView(user.getUserId(), user.getUsername(), user.getFirstName(),
                user.getLastName(), user.getEmail(), user.getUserRole().name());
    }
}
