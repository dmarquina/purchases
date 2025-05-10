package com.scoutingtcg.purchases.dto.CardForSale;

import java.util.List;

public record PokemonSinglesFilterRequest(
        List<String> sets,
        List<String> conditions,
        List<String> printings,
        String name,
        String sortByPrice
) {}
