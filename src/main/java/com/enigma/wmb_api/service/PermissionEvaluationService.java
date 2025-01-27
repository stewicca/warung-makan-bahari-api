package com.enigma.wmb_api.service;

public interface PermissionEvaluationService {
    boolean hasAccessToCustomer(String customerId, String userId);
}
