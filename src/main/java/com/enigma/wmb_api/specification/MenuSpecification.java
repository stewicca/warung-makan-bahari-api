package com.enigma.wmb_api.specification;

import com.enigma.wmb_api.constant.MenuCategory;
import com.enigma.wmb_api.dto.request.SearchMenuRequest;
import com.enigma.wmb_api.entity.Menu;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class MenuSpecification {
    public static Specification<Menu> getSpecification(SearchMenuRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(request.getQuery())) {
                Predicate queryPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), request.getQuery() + "%"),
                        criteriaBuilder.equal(root.get("category"), request.getQuery())
                );
                predicates.add(queryPredicate);
            }

            if (request.getMinPrice() != null && request.getMaxPrice() != null) {
                Predicate minMaxPredicate = criteriaBuilder.between(root.get("price"), request.getMinPrice(), request.getMaxPrice());
                predicates.add(minMaxPredicate);
            } else if (request.getMinPrice() != null) {
                Predicate minPredicate = criteriaBuilder.between(root.get("price"), request.getMinPrice(), Long.MAX_VALUE);
                predicates.add(minPredicate);
            } else if (request.getMaxPrice() != null) {
                Predicate maxPredicate = criteriaBuilder.between(root.get("price"), 0L, request.getMaxPrice());
                predicates.add(maxPredicate);
            }

            if (predicates.isEmpty()) return criteriaBuilder.conjunction();

            return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
        };
    }
}
