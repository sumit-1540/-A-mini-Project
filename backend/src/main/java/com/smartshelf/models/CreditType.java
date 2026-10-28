package com.smartshelf.models;

public enum CreditType {
    CREDIT_GIVEN,       // Customer took items on credit / Udhar
    PAYMENT_RECEIVED,   // Customer paid back Udhar
    CREDIT_ADJUSTMENT   // Manual balance correction
}
