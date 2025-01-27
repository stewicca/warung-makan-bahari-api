package com.enigma.wmb_api.dto.request;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class SearchMenuRequest extends PagingAndSortingRequest {
    private String query;
    private Long minPrice;
    private Long maxPrice;
}
