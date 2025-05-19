package com.scoutingtcg.purchases.order.dto.response;

import com.scoutingtcg.purchases.order.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

public record OrderSummaryResponse(
    String id,
    String fullName,
    Double total,
    String receiptUrl,
    LocalDateTime createdAt,
    OrderStatus status,
    Long totalItems
) {}
