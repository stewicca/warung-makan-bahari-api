package com.enigma.wmb_api.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailRequest {
    @NotBlank(message = "menu id is required")
    private String menuId;

    @NotNull(message = "quantity is required")
    private Integer quantity;
}

