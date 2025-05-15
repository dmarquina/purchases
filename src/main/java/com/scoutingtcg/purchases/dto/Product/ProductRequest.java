package com.scoutingtcg.purchases.dto.Product;

public record ProductRequest(
        Long productId,
        String productName,
        Double currentPrice,
        String franchise,
        String presentation,
        String shippingSize
) {
}
