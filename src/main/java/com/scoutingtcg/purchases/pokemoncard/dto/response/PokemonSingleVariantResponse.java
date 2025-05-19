package com.scoutingtcg.purchases.pokemoncard.dto.response;

public record PokemonSingleVariantResponse(
        Long cardForSaleId,
        String cardCondition,
        String printing,
        String franchise,
        Double price,
        Integer stock
) {
}
