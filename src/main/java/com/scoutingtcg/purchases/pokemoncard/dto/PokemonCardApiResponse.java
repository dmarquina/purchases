package com.scoutingtcg.purchases.pokemoncard.dto;

import java.util.List;

public record PokemonCardApiResponse(
        List<PokemonCardRaw> data,
        int totalCount) {
}





