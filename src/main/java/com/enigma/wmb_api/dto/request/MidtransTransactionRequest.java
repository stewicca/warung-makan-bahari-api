package com.enigma.wmb_api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MidtransTransactionRequest {
    @JsonProperty(value = "order_id")
    private String orderId;

    @JsonProperty(value = "gross_amount")
    private Long grossAmount;
}
