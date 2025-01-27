package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.service.CustomerService;
import com.enigma.wmb_api.service.PermissionEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PermissionEvaluationServiceImpl implements PermissionEvaluationService {
    private final CustomerService customerService;

    @Override
    public boolean hasAccessToCustomer(String customerId, String userId) {
        return customerService.existByCustomerIdAndUserId(customerId, userId);
    }
}
