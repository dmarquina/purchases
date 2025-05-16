package com.scoutingtcg.purchases.model.order;

public enum OrderStatus {
    WAITING_PAYMENT,
    PROCESSING_PAYMENT,
    PAID,
    SHIPPED,
    COMPLETED,
    CANCELED,
    REFUNDED
}
