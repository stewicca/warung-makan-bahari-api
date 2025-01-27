package com.enigma.wmb_api.dto.request;

import com.enigma.wmb_api.constant.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DraftOrderRequest {
    @NotBlank(message = "customer ID is required")
    private String customerId;

    @NotNull(message = "transaction type is required")
    private TransactionType transactionType;
}


