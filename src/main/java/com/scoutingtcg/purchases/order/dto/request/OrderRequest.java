package com.scoutingtcg.purchases.order.dto.request;

import com.scoutingtcg.purchases.order.dto.CartItemDto;
import com.scoutingtcg.purchases.shared.dto.AddressDto;
import com.scoutingtcg.purchases.shared.model.ShippingSize;

import java.util.List;

public record OrderRequest(
        Long userId,
        String email,
        AddressDto shippingAddress,
        String phone,
        double shippingCost,
        boolean freeShippingApplied,
        ShippingSize shippingSize,
        Double total,
        List<CartItemDto> cartItems
) {
}