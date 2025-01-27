package com.enigma.wmb_api.dto.response;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailResponse {
    private String id;
    private String menuId;
    private String menuName;
    private Integer quantity;
    private Long price;
}

