package com.thesis.electronic_invoice.service.impl;

import com.thesis.electronic_invoice.config.JwtProperties;
import com.thesis.electronic_invoice.constant.AuditAction;
import com.thesis.electronic_invoice.dto.request.LoginRequest;
import com.thesis.electronic_invoice.dto.request.ProfileUpdateRequest;
import com.thesis.electronic_invoice.dto.response.LoginResponse;
import com.thesis.electronic_invoice.repository.UsersRepository;
import com.thesis.electronic_invoice.security.JwtService;
import com.thesis.electronic_invoice.service.AuthService;
import com.thesis.electronic_invoice.service.AuditService;
import com.thesis.electronic_invoice.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.thesis.electronic_invoice.model.MstUsers;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsersRepository users;
    private final CurrentUserService currentUser;
    private final JwtProperties jwtProperties;
    private final AuditService audit;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        String token = jwtService.generateToken(
                (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal());
        MstUsers user = users.findByUsernameAndActiveTrue(request.getUsername()).orElseThrow();
        audit.recordAuthentication(user, AuditAction.LOGIN);
        return new LoginResponse(token, "Bearer", jwtProperties.getExpirationMs() / 1000,
                userView(user));
    }

    @Override
    public LoginResponse.UserView profile() {
        return userView(currentUser.get());
    }

    @Override
    public void logout() {
        audit.recordAuthentication(currentUser.get(), AuditAction.LOGOUT);
    }

    @Override
    @Transactional
    public LoginResponse.UserView updateProfile(ProfileUpdateRequest request) {
        MstUsers user = currentUser.get();
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            if (request.getPassword().length() < 8)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 8 characters");
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        return userView(users.save(user));
    }

    private LoginResponse.UserView userView(MstUsers user) {
        return new LoginResponse.UserView(user.getUserId(), user.getUsername(), user.getFirstName(),
                user.getLastName(), user.getEmail(), user.getUserRole().name());
    }
}
