package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.client.MidtransApiClient;
import com.enigma.wmb_api.client.MidtransAppClient;
import com.enigma.wmb_api.constant.Constant;
import com.enigma.wmb_api.constant.OrderStatus;
import com.enigma.wmb_api.constant.PaymentStatus;
import com.enigma.wmb_api.dto.request.*;
import com.enigma.wmb_api.dto.response.MidtransSnapResponse;
import com.enigma.wmb_api.dto.response.MidtransTransactionResponse;
import com.enigma.wmb_api.dto.response.PaymentResponse;
import com.enigma.wmb_api.entity.Order;
import com.enigma.wmb_api.entity.Payment;
import com.enigma.wmb_api.repository.PaymentRepository;
import com.enigma.wmb_api.service.OrderService;
import com.enigma.wmb_api.service.PaymentService;
import com.enigma.wmb_api.util.HashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final MidtransAppClient midtransAppClient;
    private final MidtransApiClient midtransApiClient;

    @Value("${midtrans.server.key}")
    private String MIDTRANS_SERVER_KEY;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PaymentResponse createPayment(PaymentRequest request) {
        Order order = validateOrderForPayment(request.getOrderId());
        long amount = calculateOrderAmount(order);
        MidtransSnapResponse snapTransaction = initiateMidtransPayment(order, amount);
        Payment payment = Payment.builder()
                .order(order)
                .amount(amount)
                .paymentStatus(PaymentStatus.PENDING)
                .tokenSnap(snapTransaction.getToken())
                .redirectUrl(snapTransaction.getRedirectUrl())
                .build();
        paymentRepository.saveAndFlush(payment);

        orderService.updateOrderStatus(order.getId(), OrderStatus.PENDING);
        return mapToPaymentResponse(payment);
    }

    private long calculateOrderAmount(Order order) {
        return order.getOrderDetails().stream()
                .mapToLong(detail -> detail.getQuantity() * detail.getPrice())
                .sum();
    }

    private MidtransSnapResponse initiateMidtransPayment(Order order, long amount) {
        List<MidtransItemDetailRequest> itemDetails = order.getOrderDetails().stream().map(orderDetail -> MidtransItemDetailRequest.builder()
                .id(orderDetail.getMenu().getId())
                .name(orderDetail.getMenu().getName())
                .category(orderDetail.getMenu().getCategory().getName())
                .price(orderDetail.getPrice())
                .quantity(orderDetail.getQuantity())
                .build()).toList();

        MidtransCustomerDetailsRequest customerDetailsRequest = MidtransCustomerDetailsRequest.builder()
                .firstName(order.getCustomer().getName())
                .email(order.getCustomer().getEmail())
                .phone(order.getCustomer().getPhoneNumber())
                .build();

        MidtransPaymentRequest midtransPaymentRequest = MidtransPaymentRequest.builder()
                .transactionDetail(MidtransTransactionRequest.builder()
                        .orderId(order.getId())
                        .grossAmount(amount)
                        .build())
                .enabledPayments(List.of("bca_va", "gopay", "shopeepay", "other_qris"))
                .itemDetails(itemDetails)
                .customerDetails(customerDetailsRequest)
                .build();
        String headerValue = "Basic " + Base64.getEncoder().encodeToString(MIDTRANS_SERVER_KEY.getBytes(StandardCharsets.UTF_8));
        return midtransAppClient.createSnapTransaction(midtransPaymentRequest, headerValue);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void getNotification(MidtransNotificationRequest request) {
        log.info("Start getNotification: {}", System.currentTimeMillis());
        validateSignatureKey(request.getOrderId(), request.getStatusCode(), request.getGrossAmount(), request.getSignatureKey());
        updatePaymentStatus(request.getOrderId(), request.getTransactionStatus());
        log.info("End getNotification: {}", System.currentTimeMillis());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PaymentResponse getPaymentStatusByOrderId(String orderId) {
        String headerValue = "Basic " + Base64.getEncoder().encodeToString(MIDTRANS_SERVER_KEY.getBytes(StandardCharsets.UTF_8));
        MidtransTransactionResponse transaction = midtransApiClient.getTransactionStatusByOrderId(orderId, headerValue);
        validateSignatureKey(transaction.getOrderId(), transaction.getStatusCode(), transaction.getGrossAmount(), transaction.getSignatureKey());
        Payment payment = updatePaymentStatus(transaction.getOrderId(), transaction.getTransactionStatus());
        return mapToPaymentResponse(payment);
    }

    private Payment getByOrderIdOrThrowNotFound(String orderId) {
        return paymentRepository.findByOrder_Id(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "payment order not found"));
    }

    private Order validateOrderForPayment(String orderId) {
        Order order = orderService.getOne(orderId);
        if (!order.getOrderStatus().equals(OrderStatus.DRAFT)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Constant.ERROR_CHECKOUT_NON_DRAFT);
        }
        return order;
    }

    private Payment updatePaymentStatus(String orderId, String transactionStatus) {
        Payment payment = getByOrderIdOrThrowNotFound(orderId);
        PaymentStatus newPaymentStatus = PaymentStatus.findByDesc(transactionStatus);
        payment.setPaymentStatus(newPaymentStatus);

        if (newPaymentStatus != null && newPaymentStatus.equals(PaymentStatus.SETTLEMENT)) {
            payment.getOrder().setOrderStatus(OrderStatus.CONFIRMED);
        }

        if (newPaymentStatus != null) {
            switch (newPaymentStatus) {
                case DENY, EXPIRE, CANCEL: payment.getOrder().setOrderStatus(OrderStatus.FAILED);
            }
        }

        paymentRepository.saveAndFlush(payment);
        return payment;
    }

    private void validateSignatureKey(String orderId, String statusCode, String grossAmount, String midtransSignatureKey) {
        String rawString = orderId + statusCode + grossAmount + MIDTRANS_SERVER_KEY;
        String signatureKey = HashUtil.encryptThisString(rawString);
        if (!signatureKey.equalsIgnoreCase(midtransSignatureKey)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid signature key");
        }
    }

    private PaymentResponse mapToPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .orderId(payment.getOrder().getId())
                .amount(payment.getAmount())
                .paymentStatus(payment.getPaymentStatus())
                .tokenSnap(payment.getTokenSnap())
                .redirectUrl(payment.getRedirectUrl())
                .build();
    }
}
