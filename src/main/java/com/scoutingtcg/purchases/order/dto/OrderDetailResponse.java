package com.scoutingtcg.purchases.order.dto;

import com.scoutingtcg.purchases.order.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailResponse(
        String id,
        String fullName,
        String email,
        String address,
        String apartment,
        String city,
        String state,
        String zip,
        String phone,
        String shippingSize,
        Double shippingCost,
        boolean freeShippingApplied,
        Double total,
        String receiptUrl,
        OrderStatus status,
        LocalDateTime createdAt,
        List<OrderItemDto> items
) {
}
