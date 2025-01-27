package com.enigma.wmb_api.client;

import com.enigma.wmb_api.config.FeignClientConfig;
import com.enigma.wmb_api.dto.response.MidtransTransactionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "midtransApi", url = "${midtrans.api.url}", configuration = FeignClientConfig.class)
public interface MidtransApiClient {
    @GetMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            path = "/v2/{orderId}/status"
    )
    MidtransTransactionResponse getTransactionStatusByOrderId(@PathVariable String orderId, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String headerValue);
}
