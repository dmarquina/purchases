package com.scoutingtcg.purchases.dto;

import com.scoutingtcg.purchases.model.ShippingSize;

public record ShippingSummary(
        double shippingCost,
        ShippingSize shippingSize,
        boolean freeShippingApplied
) {
}
