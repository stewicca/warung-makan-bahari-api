package com.enigma.wmb_api.service;

import com.enigma.wmb_api.dto.request.MidtransNotificationRequest;
import com.enigma.wmb_api.dto.request.PaymentRequest;
import com.enigma.wmb_api.dto.response.PaymentResponse;

public interface PaymentService {
    PaymentResponse createPayment(PaymentRequest request);
    void getNotification(MidtransNotificationRequest request);

    PaymentResponse getPaymentStatusByOrderId(String orderId);
}
