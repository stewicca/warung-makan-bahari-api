package com.enigma.wmb_api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MidtransNotificationRequest {
    @JsonProperty(value = "transaction_type")
    public String transactionType;

    @JsonProperty(value = "transaction_time")
    public String transactionTime;

    @JsonProperty(value = "transaction_status")
    public String transactionStatus;

    @JsonProperty(value = "transaction_id")
    public String transactionId;

    @JsonProperty(value = "status_message")
    public String statusMessage;

    @JsonProperty(value = "status_code")
    public String statusCode;

    @JsonProperty(value = "signature_key")
    public String signatureKey;

    @JsonProperty(value = "settlement_time")
    public String settlementTime;

    @JsonProperty(value = "reference_id")
    public String referenceId;

    @JsonProperty(value = "payment_type")
    public String paymentType;

    @JsonProperty(value = "order_id")
    public String orderId;

    @JsonProperty(value = "merchant_id")
    public String merchantId;

    @JsonProperty(value = "issuer")
    public String issuer;

    @JsonProperty(value = "gross_amount")
    public String grossAmount;

    @JsonProperty(value = "fraud_status")
    public String fraudStatus;

    @JsonProperty(value = "expiry_time")
    public String expiryTime;

    @JsonProperty(value = "currency")
    public String currency;

    @JsonProperty(value = "acquirer")
    public String acquirer;
}
