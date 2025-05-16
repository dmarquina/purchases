package com.scoutingtcg.purchases.order.model;

public enum OrderStatus {
    WAITING_PAYMENT,
    PROCESSING_PAYMENT,
    PAID,
    SHIPPED,
    COMPLETED,
    CANCELED,
    REFUNDED
}
