package com.fpt.glassesshop.constant;

import java.util.Arrays;

public enum OrderStatus {
    PENDING,
    AWAITING_PRESCRIPTION_APPROVAL,
    PREORDER_WAITING_STOCK,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REJECTED;

    public static OrderStatus from(String raw) {
        if (raw == null) return null;
        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(raw))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown OrderStatus: " + raw));
    }
}