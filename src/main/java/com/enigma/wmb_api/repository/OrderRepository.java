package com.enigma.wmb_api.repository;

import com.enigma.wmb_api.dto.response.OrderStatusSummaryResponse;
import com.enigma.wmb_api.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String>, JpaSpecificationExecutor<Order> {
    @Query("SELECT new com.enigma.wmb_api.dto.response.OrderStatusSummaryResponse(o.orderStatus, COUNT(o)) FROM Order o GROUP BY o.orderStatus")
    List<OrderStatusSummaryResponse> getOrderStatusSummary();
}
