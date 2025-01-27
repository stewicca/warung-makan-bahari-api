package com.enigma.wmb_api.repository;

import com.enigma.wmb_api.dto.response.PaymentStatusSummaryResponse;
import com.enigma.wmb_api.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByOrder_Id(String orderId);

    @Query("SELECT new com.enigma.wmb_api.dto.response.PaymentStatusSummaryResponse(p.paymentStatus, COUNT(p)) FROM Payment p GROUP BY p.paymentStatus")
    List<PaymentStatusSummaryResponse> getPaymentStatusSummary();
}
