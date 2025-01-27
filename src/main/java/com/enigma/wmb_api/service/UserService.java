package com.enigma.wmb_api.service;

import com.enigma.wmb_api.dto.request.UserRequest;
import com.enigma.wmb_api.dto.request.UserUpdatePasswordRequest;
import com.enigma.wmb_api.dto.response.UserResponse;
import com.enigma.wmb_api.entity.UserAccount;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserResponse create(UserRequest request);
    UserAccount create(UserAccount userAccount);
    UserAccount getById(String id);
    UserResponse getAuthentication();

    void updatePassword(String id, UserUpdatePasswordRequest request);
}
