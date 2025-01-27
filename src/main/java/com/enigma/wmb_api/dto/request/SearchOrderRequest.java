package com.enigma.wmb_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class SearchOrderRequest extends PagingAndSortingRequest{
    private String orderId;
    private String query;

    @NotBlank(message = "start date is required")
    private String startDate;

    @NotBlank(message = "end date is required")
    private String endDate;
}
