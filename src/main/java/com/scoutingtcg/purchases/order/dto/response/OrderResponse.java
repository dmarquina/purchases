package com.scoutingtcg.purchases.order.dto.response;


import com.scoutingtcg.purchases.order.model.OrderStatus;
import com.scoutingtcg.purchases.shared.dto.AddressDto;

import java.time.LocalDateTime;

public record OrderResponse(
        String id,
        String email,
        AddressDto shippingAddress,
        String receiptUrl,
        OrderStatus status,
        LocalDateTime createdAt
) {
}
