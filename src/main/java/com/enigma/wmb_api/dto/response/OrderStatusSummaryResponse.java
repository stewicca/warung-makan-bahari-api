package com.enigma.wmb_api.dto.response;

import com.enigma.wmb_api.constant.OrderStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusSummaryResponse {
    private OrderStatus orderStatus;
    private Long count;
}
