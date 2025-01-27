package com.enigma.wmb_api.dto.response;

import com.enigma.wmb_api.constant.OrderStatus;
import com.enigma.wmb_api.constant.TransactionType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    private String id;
    private String customerId;
    private String customerName;
    private String transactionDate;
    private TransactionType transactionType;
    private OrderStatus orderStatus;
    private List<OrderDetailResponse> orderDetails;

    public OrderResponse(String id, String customerId, String customerName, String transactionDate, TransactionType transactionType, OrderStatus orderStatus) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.transactionDate = transactionDate;
        this.transactionType = transactionType;
        this.orderStatus = orderStatus;
    }
}

