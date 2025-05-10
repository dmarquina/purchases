package com.scoutingtcg.purchases.dto.CardForSale;

public record PokemonSingleVariantResponse(
        Long cardForSaleId,
        String cardCondition,
        String printing,
        String franchise,
        Double price,
        Integer stock
) {
}
