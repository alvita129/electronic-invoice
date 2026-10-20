package com.thesis.electronic_invoice.service.impl;

import com.thesis.electronic_invoice.model.MstUsers;
import com.thesis.electronic_invoice.repository.UsersRepository;
import com.thesis.electronic_invoice.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CurrentUserServiceImpl implements CurrentUserService {
    private final UsersRepository users;

    public MstUsers get() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return users.findByUsernameAndActiveTrue(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User no longer exists"));
    }
}
