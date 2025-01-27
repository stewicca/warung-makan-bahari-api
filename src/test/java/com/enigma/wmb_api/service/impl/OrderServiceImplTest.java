package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.Constant;
import com.enigma.wmb_api.constant.OrderStatus;
import com.enigma.wmb_api.constant.TransactionType;
import com.enigma.wmb_api.dto.request.DraftOrderRequest;
import com.enigma.wmb_api.dto.request.OrderDetailRequest;
import com.enigma.wmb_api.dto.response.OrderResponse;
import com.enigma.wmb_api.entity.Customer;
import com.enigma.wmb_api.entity.Menu;
import com.enigma.wmb_api.entity.Order;
import com.enigma.wmb_api.entity.OrderDetail;
import com.enigma.wmb_api.repository.OrderRepository;
import com.enigma.wmb_api.service.CustomerService;
import com.enigma.wmb_api.service.MenuService;
import com.enigma.wmb_api.util.ValidationUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CustomerService customerService;
    @Mock
    private MenuService menuService;
    @Mock
    private ValidationUtil validationUtil;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void shouldReturnOrderResponseWhenCreateDraft() {
        DraftOrderRequest request = DraftOrderRequest.builder()
                .customerId("cust-1")
                .transactionType(TransactionType.TA)
                .build();

        Mockito.doNothing().when(validationUtil)
                .validate(request);

        Customer mockCustomer = Customer.builder()
                .id(request.getCustomerId())
                .build();
        Mockito.when(customerService.getOne(request.getCustomerId()))
                .thenReturn(mockCustomer);

        Order expectedOrder = Order.builder()
                .id("order-1")
                .customer(mockCustomer)
                .transactionType(request.getTransactionType())
                .orderStatus(OrderStatus.DRAFT)
                .orderDetails(new ArrayList<>())
                .build();

        Mockito.when(orderRepository.saveAndFlush(Mockito.any()))
                .thenReturn(expectedOrder);

        OrderResponse result = orderService.createDraft(request);

        assertEquals(expectedOrder.getId(), result.getId());
        assertEquals(expectedOrder.getTransactionType(), result.getTransactionType());
        assertNotNull(result.getCustomerId());
    }

    @Test
    void shouldThrowErrorWhenAddOrderDetail() {
        String orderId = "order-1";
        OrderDetailRequest request = new OrderDetailRequest();
        request.setMenuId("menu-1");
        request.setQuantity(1);

        Mockito.doNothing().when(validationUtil)
                .validate(request);

        Order mockOrder = new Order();
        mockOrder.setOrderStatus(OrderStatus.PENDING);
        mockOrder.setOrderDetails(List.of());

        Mockito.when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(mockOrder));

        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> orderService.addOrderDetail(orderId, request));

        assertEquals(Constant.ERROR_ADD_ITEMS_TO_NON_DRAFT, e.getReason());
    }

    @Test
    void shouldIncreaseQuantityAndReturnOrderResponseWhenAddOrderDetail() {
        String orderId = "order-1";
        OrderDetailRequest request = new OrderDetailRequest();
        request.setMenuId("menu-1");
        request.setQuantity(1);

        Mockito.doNothing().when(validationUtil)
                .validate(request);

        Menu mockMenu = new Menu();
        mockMenu.setId(request.getMenuId());
        mockMenu.setPrice(2000L);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setId("order-detail-1");
        orderDetail.setQuantity(1);
        orderDetail.setMenu(mockMenu);

        Customer mockCustomer = new Customer();
        mockCustomer.setId("cust-1");
        mockCustomer.setName("budi");

        Order mockOrder = new Order();
        mockOrder.setOrderStatus(OrderStatus.DRAFT);
        mockOrder.setOrderDetails(List.of(orderDetail));
        mockOrder.setCustomer(mockCustomer);

        Mockito.when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(mockOrder));

        Mockito.when(menuService.getOne(Mockito.anyString()))
                        .thenReturn(mockMenu);

        Mockito.when(orderRepository.save(Mockito.any()))
                .thenReturn(mockOrder);

        OrderResponse result = orderService.addOrderDetail(orderId, request);

        assertEquals(2, result.getOrderDetails().get(0).getQuantity());
    }

    @Test
    void shouldAddNewDetailAndReturnOrderResponseWhenAddOrderDetail() {
        String orderId = "order-1";
        OrderDetailRequest request = new OrderDetailRequest();
        request.setMenuId("menu-1");
        request.setQuantity(1);

        Mockito.doNothing().when(validationUtil)
                .validate(request);

        Menu mockMenu = new Menu();
        mockMenu.setId(request.getMenuId());
        mockMenu.setPrice(2000L);

        Customer mockCustomer = new Customer();
        mockCustomer.setId("cust-1");
        mockCustomer.setName("budi");

        Order mockOrder = new Order();
        mockOrder.setOrderStatus(OrderStatus.DRAFT);
        mockOrder.setOrderDetails(new ArrayList<>());
        mockOrder.setCustomer(mockCustomer);

        Mockito.when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(mockOrder));

        Mockito.when(menuService.getOne(Mockito.anyString()))
                .thenReturn(mockMenu);

        Mockito.when(orderRepository.save(Mockito.any()))
                .thenReturn(mockOrder);

        OrderResponse result = orderService.addOrderDetail(orderId, request);

        assertEquals(1, result.getOrderDetails().size());
    }
}