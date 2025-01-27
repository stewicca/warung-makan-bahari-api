package com.enigma.wmb_api.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    SETTLEMENT("settlement"),
    PENDING("pending"),
    DENY("deny"),
    CANCEL("cancel"),
    EXPIRE("expire");

    private final String description;

    public static PaymentStatus findByDesc(String desc) {
        for (PaymentStatus value : values()) {
            if (value.getDescription().equalsIgnoreCase(desc)) {
                return value;
            }
        }
        return null;
    }
}
