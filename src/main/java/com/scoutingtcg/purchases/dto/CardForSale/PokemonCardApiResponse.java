package com.scoutingtcg.purchases.dto.CardForSale;

import lombok.Data;

import java.util.List;

@Data
public class PokemonCardApiResponse {
    private List<PokemonCardRaw> data;
    private int totalCount;


}





