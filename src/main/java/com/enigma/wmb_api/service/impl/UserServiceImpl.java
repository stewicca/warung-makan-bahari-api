package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.Constant;
import com.enigma.wmb_api.constant.UserRole;
import com.enigma.wmb_api.dto.request.UserRequest;
import com.enigma.wmb_api.dto.request.UserUpdatePasswordRequest;
import com.enigma.wmb_api.dto.response.UserResponse;
import com.enigma.wmb_api.entity.UserAccount;
import com.enigma.wmb_api.repository.UserAccountRepository;
import com.enigma.wmb_api.service.UserService;
import com.enigma.wmb_api.util.ValidationUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final ValidationUtil validationUtil;

    @Value("${warung.makan.bahari.user-admin}")
    private String USERNAME_ADMIN;

    @Value("${warung.makan.bahari.user-password}")
    private String PASSWORD_ADMIN;


    @Transactional(rollbackFor = Exception.class)
    @PostConstruct
    public void initUser() {
        boolean exist = userAccountRepository.existsByUsername(USERNAME_ADMIN);
        if (exist) return;
        UserAccount userAccount = UserAccount.builder()
                .username(USERNAME_ADMIN)
                .password(passwordEncoder.encode(PASSWORD_ADMIN))
                .role(UserRole.ROLE_ADMIN)
                .build();
        userAccountRepository.saveAndFlush(userAccount);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserResponse create(UserRequest request) {
        validationUtil.validate(request);
        try {
            UserRole userRole = UserRole.findByDescription(request.getRole());
            if (userRole == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, Constant.ERROR_ROLE_NOT_FOUND);
            UserAccount userAccount = UserAccount.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(userRole)
                    .build();
            userAccountRepository.saveAndFlush(userAccount);
            return toResponse(userAccount);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, Constant.ERROR_USERNAME_DUPLICATE);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserAccount create(UserAccount userAccount) {
        userAccount.setPassword(passwordEncoder.encode(userAccount.getPassword()));
        return userAccountRepository.saveAndFlush(userAccount);
    }

    @Transactional(readOnly = true)
    @Override
    public UserAccount getById(String id) {
        return userAccountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Override
    public UserResponse getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccount userAccount = (UserAccount) authentication.getPrincipal();
        return toResponse(userAccount);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updatePassword(String id, UserUpdatePasswordRequest request) {
        validationUtil.validate(request);
        UserAccount userAccount = getById(id);

        if (!passwordEncoder.matches(userAccount.getPassword(), request.getCurrentPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Constant.INVALID_CREDENTIAL);
        }

        userAccount.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userAccountRepository.saveAndFlush(userAccount);
    }

    public UserResponse toResponse(UserAccount userAccount) {
        return UserResponse.builder()
                .id(userAccount.getId())
                .username(userAccount.getUsername())
                .role(userAccount.getRole().getDescription())
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userAccountRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }
}
