package com.enigma.wmb_api.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TransactionType {
    DI("Dine In"),
    TA("Take Away");

    private final String description;
}
