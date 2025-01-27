package com.enigma.wmb_api.service;

import com.enigma.wmb_api.constant.OrderStatus;
import com.enigma.wmb_api.dto.request.DraftOrderRequest;
import com.enigma.wmb_api.dto.request.OrderDetailRequest;
import com.enigma.wmb_api.dto.request.SearchOrderRequest;
import com.enigma.wmb_api.dto.request.UpdateOrderStatusRequest;
import com.enigma.wmb_api.dto.response.OrderDetailResponse;
import com.enigma.wmb_api.dto.response.OrderResponse;
import com.enigma.wmb_api.entity.Order;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {
    OrderResponse createDraft(DraftOrderRequest request);
    List<OrderDetailResponse> getOrderDetails(String orderId);
    OrderResponse addOrderDetail(String orderId, OrderDetailRequest request);
    OrderResponse updateOrderDetail(String orderId, String detailId, OrderDetailRequest request);
    OrderResponse removeOrderDetail(String orderId, String detailId);
    OrderResponse updateOrderStatus(String orderId, UpdateOrderStatusRequest request);
    void updateOrderStatus(String orderId, OrderStatus orderStatus);
    Page<OrderResponse> getAllOrders(SearchOrderRequest request);
    OrderResponse getOrderById(String id);
    Order getOne(String id);
}
