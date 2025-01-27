package com.enigma.wmb_api.service;

import com.enigma.wmb_api.dto.request.CustomerCreateRequest;
import com.enigma.wmb_api.dto.request.CustomerUpdateRequest;
import com.enigma.wmb_api.dto.request.SearchCustomerRequest;
import com.enigma.wmb_api.dto.response.CustomerResponse;
import com.enigma.wmb_api.entity.Customer;
import org.springframework.data.domain.Page;

public interface CustomerService {
    CustomerResponse create(CustomerCreateRequest request);
    CustomerResponse getById(String id);
    Customer getOne(String id);
    Customer getByUserId(String userId);
    Page<CustomerResponse> getAll(SearchCustomerRequest request);
    CustomerResponse update(String id, CustomerUpdateRequest request);
    void deleteById(String id);

    boolean existByCustomerIdAndUserId(String customerId, String userId);
}
