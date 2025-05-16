package com.scoutingtcg.purchases.order.dto;


import com.scoutingtcg.purchases.order.model.OrderStatus;

import java.time.LocalDateTime;

public record OrderResponse(
        String id,
        String fullName,
        String email,
        String address,
        String city,
        String state,
        Double total,
        String receiptUrl,
        OrderStatus status,
        LocalDateTime createdAt
) {
}
