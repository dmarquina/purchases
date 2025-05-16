package com.scoutingtcg.purchases.order.dto;

import com.scoutingtcg.purchases.shared.model.ShippingSize;
import lombok.Data;

import java.util.List;

public record OrderRequest(
    Long userId,
    String email,
    String fullName,
    String address,
    String apartment,
    String phone,
    String city,
    String state,
    String zip,
    double shippingCost,
    boolean freeShippingApplied,
    ShippingSize shippingSize,
    Double total,
    List<CartItemDto> cartItems
) {
}