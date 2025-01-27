package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.Constant;
import com.enigma.wmb_api.constant.OrderStatus;
import com.enigma.wmb_api.constant.UserRole;
import com.enigma.wmb_api.dto.request.DraftOrderRequest;
import com.enigma.wmb_api.dto.request.OrderDetailRequest;
import com.enigma.wmb_api.dto.request.SearchOrderRequest;
import com.enigma.wmb_api.dto.request.UpdateOrderStatusRequest;
import com.enigma.wmb_api.dto.response.OrderDetailResponse;
import com.enigma.wmb_api.dto.response.OrderResponse;
import com.enigma.wmb_api.entity.*;
import com.enigma.wmb_api.repository.OrderRepository;
import com.enigma.wmb_api.service.CustomerService;
import com.enigma.wmb_api.service.MenuService;
import com.enigma.wmb_api.service.OrderService;
import com.enigma.wmb_api.specification.OrderSpecification;
import com.enigma.wmb_api.util.DateUtil;
import com.enigma.wmb_api.util.SortUtil;
import com.enigma.wmb_api.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final MenuService menuService;
    private final ValidationUtil validationUtil;

    @Override
    public OrderResponse createDraft(DraftOrderRequest request) {
        validationUtil.validate(request);
        Customer customer = customerService.getOne(request.getCustomerId());
        Order draftOrder = Order.builder()
                .customer(customer)
                .transactionType(request.getTransactionType())
                .orderStatus(OrderStatus.DRAFT)
                .orderDetails(new ArrayList<>())
                .build();
        Order savedOrder = orderRepository.saveAndFlush(draftOrder);
        return mapToOrderResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderDetailResponse> getOrderDetails(String orderId) {
        Order order = getOne(orderId);
        return order.getOrderDetails().stream()
                .map(this::mapToOrderDetailResponse)
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public OrderResponse addOrderDetail(String orderId, OrderDetailRequest request) {
        validationUtil.validate(request);
        Order order = getOne(orderId);
        if (order.getOrderStatus() != OrderStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Constant.ERROR_ADD_ITEMS_TO_NON_DRAFT);
        }
        Menu menu = menuService.getOne(request.getMenuId());

        Optional<OrderDetail> existingOrderDetail = order.getOrderDetails().stream()
                .filter(detail -> detail.getMenu().getId().equals(menu.getId()))
                .findFirst();

        if (existingOrderDetail.isPresent()) {
            OrderDetail orderDetail = existingOrderDetail.get();
            orderDetail.setQuantity(orderDetail.getQuantity() + request.getQuantity());
            orderDetail.setPrice(menu.getPrice());
        } else {
            OrderDetail newOrderDetail = OrderDetail.builder()
                    .menu(menu)
                    .order(order)
                    .quantity(request.getQuantity())
                    .price(menu.getPrice())
                    .build();
            order.getOrderDetails().add(newOrderDetail);
        }

        Order updatedOrder = orderRepository.save(order);
        return mapToOrderResponse(updatedOrder);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public OrderResponse updateOrderDetail(String orderId, String detailId, OrderDetailRequest request) {
        validationUtil.validate(request);
        Order order = getOne(orderId);
        if (order.getOrderStatus() != OrderStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Constant.ERROR_UPDATE_ITEMS_IN_NON_DRAFT);
        }

        OrderDetail orderDetail = order.getOrderDetails().stream()
                .filter(detail -> detail.getId().equals(detailId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Constant.ERROR_ORDER_DETAIL_NOT_FOUND));

        Menu menu = menuService.getOne(request.getMenuId());
        orderDetail.setMenu(menu);
        orderDetail.setQuantity(request.getQuantity());
        orderDetail.setPrice(menu.getPrice());

        Order updatedOrder = orderRepository.save(order);
        return mapToOrderResponse(updatedOrder);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public OrderResponse removeOrderDetail(String orderId, String detailId) {
        Order order = getOne(orderId);
        if (order.getOrderStatus() != OrderStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Constant.ERROR_REMOVE_ITEMS_FROM_NON_DRAFT);
        }

        order.getOrderDetails().removeIf(detail -> detail.getId().equals(detailId));
        Order updatedOrder = orderRepository.save(order);
        return mapToOrderResponse(updatedOrder);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public OrderResponse updateOrderStatus(String orderId, UpdateOrderStatusRequest request) {
        validationUtil.validate(request);
        Order order = getOne(orderId);
        order.setOrderStatus(request.getStatus());
        Order updatedOrder = orderRepository.save(order);
        return mapToOrderResponse(updatedOrder);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateOrderStatus(String orderId, OrderStatus orderStatus) {
        Order order = getOne(orderId);
        order.setOrderStatus(orderStatus);
        Order updatedOrder = orderRepository.save(order);
        mapToOrderResponse(updatedOrder);
    }

    public Page<OrderResponse> getAllOrders(SearchOrderRequest request) {
        validationUtil.validate(request);
        Sort sort = SortUtil.parseSort(request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Specification<Order> specification = buildOrderSpecification(request);

        return orderRepository.findAll(specification, pageable).map(this::mapToOrderResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public Order getOne(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Constant.ERROR_ORDER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    @Override
    public OrderResponse getOrderById(String id) {
        return mapToOrderResponse(getOne(id));
    }

    private Specification<Order> buildOrderSpecification(SearchOrderRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccount userAccount = (UserAccount) authentication.getPrincipal();

        Specification<Order> specification = Specification.where(null);

        if (request.getStartDate() != null && request.getEndDate() != null) {
            specification = specification.and(OrderSpecification.orderWithinDateRange(request.getStartDate(), request.getEndDate()));
        }

        if (userAccount.getRole().equals(UserRole.ROLE_CUSTOMER)) {
            Customer customer = customerService.getByUserId(userAccount.getId());
            specification = specification.and(OrderSpecification.orderByCustomer(customer));
        }

        return specification;
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderDetailResponse> orderDetailResponses = order.getOrderDetails().stream()
                .map(this::mapToOrderDetailResponse)
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomer().getId())
                .customerName(order.getCustomer().getName())
                .transactionType(order.getTransactionType())
                .transactionDate(DateUtil.localDateTimeToString(order.getTransactionDate()))
                .orderStatus(order.getOrderStatus())
                .orderDetails(orderDetailResponses)
                .build();
    }

    private OrderDetailResponse mapToOrderDetailResponse(OrderDetail detail) {
        return OrderDetailResponse.builder()
                .id(detail.getId())
                .menuId(detail.getMenu().getId())
                .menuName(detail.getMenu().getName())
                .quantity(detail.getQuantity())
                .price(detail.getMenu().getPrice())
                .build();
    }
}

