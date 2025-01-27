package com.enigma.wmb_api.dto.response;

import com.enigma.wmb_api.constant.PaymentStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse {
    private String orderId;
    private Long amount;
    private PaymentStatus paymentStatus;
    private String tokenSnap;
    private String redirectUrl;
}
