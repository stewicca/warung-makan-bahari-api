package com.enigma.wmb_api.dto.request;

import com.enigma.wmb_api.constant.MenuCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MenuRequest {
    @NotBlank(message = "name is required")
    private String name;

    @NotNull(message = "price is required")
    @Min(value = 1, message = "price cannot be zero or negative value")
    private Long price;

    @NotNull(message = "category is required")
    private MenuCategory category;
}
