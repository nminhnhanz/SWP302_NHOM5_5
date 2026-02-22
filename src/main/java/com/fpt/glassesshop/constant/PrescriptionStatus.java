package com.fpt.glassesshop.constant;

import java.util.Arrays;

public enum PrescriptionStatus {
    PENDING, APPROVED, REJECTED;

    public static PrescriptionStatus from(String raw) {
        if (raw == null) return null;
        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(raw))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown PrescriptionStatus: " + raw));
    }
}