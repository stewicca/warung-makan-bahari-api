package com.enigma.wmb_api.dto.request;

import com.enigma.wmb_api.constant.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOrderStatusRequest {
    @NotNull(message = "status is required")
    private OrderStatus status;
}

