package com.scoutingtcg.purchases.pokemoncard.dto;

import java.util.List;

public record PokemonSinglesPageResponse(
        List<PokemonSingleResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
}
