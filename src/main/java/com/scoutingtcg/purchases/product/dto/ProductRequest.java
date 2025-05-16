package com.scoutingtcg.purchases.product.dto;

import com.scoutingtcg.purchases.shared.model.Franchise;

public record ProductRequest(
        Long productId,
        String productName,
        Double currentPrice,
        Franchise franchise,
        String presentation,
        String shippingSize
) {
}
