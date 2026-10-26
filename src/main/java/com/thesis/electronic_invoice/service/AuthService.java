package com.thesis.electronic_invoice.service;

import com.thesis.electronic_invoice.dto.request.LoginRequest;
import com.thesis.electronic_invoice.dto.request.ChangePasswordRequest;
import com.thesis.electronic_invoice.dto.request.ProfileUpdateRequest;
import com.thesis.electronic_invoice.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    LoginResponse.UserView profile();
    void logout();
    LoginResponse.UserView updateProfile(ProfileUpdateRequest request);
    void changePassword(ChangePasswordRequest request);
}
