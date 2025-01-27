package com.enigma.wmb_api.dto.response;

import com.enigma.wmb_api.constant.MenuCategory;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MenuResponse {
    private String id;
    private String name;
    private Long price;
    private MenuCategory category;
    private List<FileResponse> images;
}
