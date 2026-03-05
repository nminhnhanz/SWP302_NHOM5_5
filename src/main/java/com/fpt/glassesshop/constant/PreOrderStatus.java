package com.fpt.glassesshop.constant;

import java.util.Arrays;

public enum PreOrderStatus {
    WAITING_STOCK, ARRIVED, CANCELLED;

    public static PreOrderStatus from(String raw) {
        if (raw == null) return null;
        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(raw))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown PreOrderStatus: " + raw));
    }
}