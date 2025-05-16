package com.scoutingtcg.purchases.shipping.dto;

import com.scoutingtcg.purchases.shared.model.ShippingSize;

public record ShippingSummary(
        double shippingCost,
        ShippingSize shippingSize,
        boolean freeShippingApplied
) {
}
