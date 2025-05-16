package com.scoutingtcg.purchases.pokemoncard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class PokemonCardMarketPriceDto {

    private String id;
    private Map<String, Double> tcgPlayerPrices;
}
