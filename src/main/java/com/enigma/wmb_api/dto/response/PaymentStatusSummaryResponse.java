package com.enigma.wmb_api.dto.response;

import com.enigma.wmb_api.constant.PaymentStatus;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentStatusSummaryResponse {
    private PaymentStatus paymentStatus;
    private Long count;
}
