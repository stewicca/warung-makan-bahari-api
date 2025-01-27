package com.enigma.wmb_api.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MidtransItemDetailRequest {
    private String id;
    private Long price;
    private Integer quantity;
    private String name;
    private String category;
}
