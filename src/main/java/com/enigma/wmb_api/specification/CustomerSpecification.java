package com.enigma.wmb_api.specification;

import com.enigma.wmb_api.entity.Customer;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class CustomerSpecification {
    public static Specification<Customer> getSpecification(String q) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(q)) return criteriaBuilder.conjunction();

            Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), q + "%");
            Predicate emailPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), q + "%");
            Predicate phoneNumberPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("phoneNumber")), q + "%");

            return criteriaBuilder.or(namePredicate, emailPredicate, phoneNumberPredicate);
        };
    }
}
