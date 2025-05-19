package com.scoutingtcg.purchases.order.dto.response;

import com.scoutingtcg.purchases.order.dto.OrderItemDto;
import com.scoutingtcg.purchases.order.model.OrderStatus;
import com.scoutingtcg.purchases.shared.dto.AddressDto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailResponse(
        String id,
        String email,
        String phone,
        AddressDto shippingAddress,
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
