package com.enigma.wmb_api.dto.request;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PagingAndSortingRequest {
    private Integer page;
    private Integer size;
    private String sortBy;

    public Integer getPage() {
        return page <= 0 ? 0 : page - 1;
    }
}
