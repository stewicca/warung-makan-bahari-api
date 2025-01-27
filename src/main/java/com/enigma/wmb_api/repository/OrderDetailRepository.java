package com.enigma.wmb_api.repository;

import com.enigma.wmb_api.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, String> {
    @Query("SELECT SUM(od.price * od.quantity) FROM OrderDetail od WHERE od.order.orderStatus = 'COMPLETED' GROUP BY od.order")
    BigDecimal getTotalSales();
}
