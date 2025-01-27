package com.enigma.wmb_api.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagingResponse {
    private Long totalItems;
    private Integer totalPages;
    private Integer page;
    private Integer size;
}
