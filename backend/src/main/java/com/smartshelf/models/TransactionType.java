package com.smartshelf.models;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TransactionType {
    SALE,
    RESTOCK,
    ADJUSTMENT_DAMAGE,
    ADJUSTMENT_LOST,
    ADJUSTMENT_EXPIRED,
    ADJUSTMENT_MANUAL,
    RETURN;

    @JsonCreator
    public static TransactionType fromString(String value) {
        if (value == null) return ADJUSTMENT_MANUAL;
        String val = value.trim().toUpperCase();
        if (val.equals("EXPIRED_WRITE_OFF") || val.equals("EXPIRED") || val.equals("ADJUSTMENT_EXPIRED")) {
            return ADJUSTMENT_EXPIRED;
        }
        if (val.equals("DAMAGE_WRITE_OFF") || val.equals("DAMAGE") || val.equals("ADJUSTMENT_DAMAGE")) {
            return ADJUSTMENT_DAMAGE;
        }
        if (val.equals("LOST") || val.equals("ADJUSTMENT_LOST")) {
            return ADJUSTMENT_LOST;
        }
        try {
            return TransactionType.valueOf(val);
        } catch (IllegalArgumentException e) {
            return ADJUSTMENT_MANUAL;
        }
    }
}

