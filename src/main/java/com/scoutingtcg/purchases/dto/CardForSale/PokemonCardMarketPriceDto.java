package com.scoutingtcg.purchases.dto.CardForSale;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class PokemonCardMarketPriceDto {

    private String id;
    private Map<String, Double> tcgPlayerPrices;
}
