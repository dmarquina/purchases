package com.scoutingtcg.purchases.order.dto;

public record OrderItemDto(

        Long id,
        Long productOrCardForSaleId,
        String image,
        String name,
        int quantity,
        double price,
        String presentation,
        String franchise
) { }