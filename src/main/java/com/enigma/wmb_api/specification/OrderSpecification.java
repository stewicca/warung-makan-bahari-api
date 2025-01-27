package com.enigma.wmb_api.specification;

import com.enigma.wmb_api.entity.Customer;
import com.enigma.wmb_api.entity.Order;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecification {

    public static Specification<Order> orderByCustomer(Customer customer) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("customer"), customer);
    }

    public static Specification<Order> orderWithinDateRange(String startDate, String endDate) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("createdAt"), startDate, endDate);
    }

}
