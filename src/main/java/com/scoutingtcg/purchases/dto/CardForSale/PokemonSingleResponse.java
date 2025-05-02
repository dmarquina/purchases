package com.scoutingtcg.purchases.dto.CardForSale;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class PokemonSingleResponse {
    String cardId;
    String cardName;
    String imageUrl;
    String setName;
    String rarity;
    String number;
    List<PokemonSingleVariantResponse> variants;
}