package com.scoutingtcg.purchases.shipping.dto;

import com.scoutingtcg.purchases.order.dto.CartItemDto;

import java.util.List;

public record ShippingCalculateRequest(
        String destinationState,
        List<CartItemDto> cartItems
) {
}
