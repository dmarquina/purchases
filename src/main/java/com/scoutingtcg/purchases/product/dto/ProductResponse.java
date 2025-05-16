package com.scoutingtcg.purchases.product.dto;


public record ProductResponse(
        String id,
        String cardId,
        String cardName,
        String cardImage,
        String cardSet,
        String vendorId,
        Double price,
        Integer quantity
) {
}
