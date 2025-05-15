package com.scoutingtcg.purchases.dto;

import java.util.List;

public record ShippingCalculateRequest(
        String destinationState,
        List<CartItemDto> cartItems
) {
}
