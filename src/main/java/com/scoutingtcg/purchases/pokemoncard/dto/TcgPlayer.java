package com.scoutingtcg.purchases.pokemoncard.dto;

import java.util.Map;

public record TcgPlayer(
        Map<String, PriceEntry> prices
) {
}