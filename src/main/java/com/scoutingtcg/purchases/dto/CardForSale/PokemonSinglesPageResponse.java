package com.scoutingtcg.purchases.dto.CardForSale;

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
