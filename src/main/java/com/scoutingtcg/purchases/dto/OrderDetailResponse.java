package com.scoutingtcg.purchases.dto;

import com.scoutingtcg.purchases.model.OrderItemDto;
import com.scoutingtcg.purchases.model.OrderStatus;

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
        Double total,
        String receiptUrl,
        OrderStatus status,
        LocalDateTime createdAt,
        List<OrderItemDto> items
) {
}
