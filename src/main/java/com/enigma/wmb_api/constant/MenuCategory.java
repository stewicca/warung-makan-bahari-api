package com.enigma.wmb_api.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MenuCategory {
    FOOD("Makanan"),
    BEVERAGE("Minuman");

    private final String name;
}
