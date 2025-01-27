package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.Constant;
import com.enigma.wmb_api.constant.UserRole;
import com.enigma.wmb_api.dto.request.CustomerCreateRequest;
import com.enigma.wmb_api.dto.request.CustomerUpdateRequest;
import com.enigma.wmb_api.dto.request.SearchCustomerRequest;
import com.enigma.wmb_api.dto.response.CustomerResponse;
import com.enigma.wmb_api.entity.Customer;
import com.enigma.wmb_api.entity.UserAccount;
import com.enigma.wmb_api.repository.CustomerRepository;
import com.enigma.wmb_api.service.CustomerService;
import com.enigma.wmb_api.service.UserService;
import com.enigma.wmb_api.specification.CustomerSpecification;
import com.enigma.wmb_api.util.SortUtil;
import com.enigma.wmb_api.util.ValidationUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final UserService userService;
    private final ValidationUtil validationUtil;

    @PostConstruct
    protected void initGuest() {
        final String GUEST_NAME = "GUEST";
        boolean exists = customerRepository.existsByNameIgnoreCase(GUEST_NAME);
        if (exists) return;
        customerRepository.saveAndFlush(Customer.builder()
                .name(GUEST_NAME)
                .phoneNumber("0812345678")
                .email("guest@wmb.com")
                .build());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CustomerResponse create(CustomerCreateRequest request) {
        validationUtil.validate(request);
        UserAccount userAccount = UserAccount.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .role(UserRole.ROLE_CUSTOMER)
                .build();
        userService.create(userAccount);
        Customer customer = Customer.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .userAccount(userAccount)
                .build();
        customerRepository.saveAndFlush(customer);
        return toResponse(customer);
    }

    @Transactional(readOnly = true)
    @Override
    public CustomerResponse getById(String id) {
        return toResponse(getOne(id));
    }

    @Override
    public Customer getOne(String id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Constant.ERROR_CUSTOMER_NOT_FOUND));
    }

    @Override
    public Customer getByUserId(String userId) {
        return customerRepository.findByUserAccount_Id(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Constant.ERROR_CUSTOMER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CustomerResponse> getAll(SearchCustomerRequest request) {
        Sort sort = SortUtil.parseSort(request.getSortBy());
        Specification<Customer> specification = CustomerSpecification.getSpecification(request.getQuery());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
        return customerRepository.findAll(specification, pageable).map(this::toResponse);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CustomerResponse update(String id, CustomerUpdateRequest request) {
        validationUtil.validate(request);
        Customer customer = getOne(id);
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhoneNumber(request.getPhoneNumber());
        customerRepository.save(customer);
        return toResponse(customer);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteById(String id) {
        Customer customer = getOne(id);
        customerRepository.delete(customer);
    }

    @Override
    public boolean existByCustomerIdAndUserId(String customerId, String userId) {
        return customerRepository.existsByIdAndUserAccount_Id(customerId, userId);
    }

    private CustomerResponse toResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .userId(customer.getUserAccount().getId())
                .build();
    }
}
