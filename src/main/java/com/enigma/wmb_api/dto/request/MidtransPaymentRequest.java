package com.enigma.wmb_api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MidtransPaymentRequest {
    @JsonProperty(value = "transaction_details")
    private MidtransTransactionRequest transactionDetail;

    @JsonProperty(value = "enabled_payments")
    private List<String> enabledPayments;

    @JsonProperty(value = "item_details")
    private List<MidtransItemDetailRequest> itemDetails;

    @JsonProperty(value = "customer_details")
    private MidtransCustomerDetailsRequest customerDetails;
}
