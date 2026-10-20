package com.thesis.electronic_invoice.service;

import com.thesis.electronic_invoice.dto.request.UserRequest;
import com.thesis.electronic_invoice.dto.response.LoginResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<LoginResponse.UserView> list();

    LoginResponse.UserView create(UserRequest request);

    LoginResponse.UserView update(UUID id, UserRequest request);

    void delete(UUID id);
}
