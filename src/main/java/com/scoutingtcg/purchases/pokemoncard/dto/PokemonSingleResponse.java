package com.scoutingtcg.purchases.pokemoncard.dto;

import java.util.List;

public record PokemonSingleResponse(
        String cardId,
        String cardName,
        String imageUrl,
        String setName,
        String rarity,
        String number,
        String franchise,
        List<PokemonSingleVariantResponse> variants
) {
}