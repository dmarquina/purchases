package com.scoutingtcg.purchases.pokemoncard.dto.api;

import java.util.Map;

public record TcgPlayer(
        Map<String, PriceEntry> prices
) {
}