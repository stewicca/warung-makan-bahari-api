package com.enigma.wmb_api.repository;

import com.enigma.wmb_api.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, String>, JpaSpecificationExecutor<Customer> {
    boolean existsByIdAndUserAccount_Id(String id, String userId);
    boolean existsByNameIgnoreCase(String name);
    Optional<Customer> findByUserAccount_Id(String userId);
}
