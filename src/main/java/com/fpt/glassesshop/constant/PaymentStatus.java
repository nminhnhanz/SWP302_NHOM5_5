package com.fpt.glassesshop.constant;

import java.util.Arrays;

public enum PaymentStatus {
    UNPAID, PAID, FAILED, REFUNDED;

    public static PaymentStatus from(String raw) {
        if (raw == null) return null;
        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(raw))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown PaymentStatus: " + raw));
    }
}