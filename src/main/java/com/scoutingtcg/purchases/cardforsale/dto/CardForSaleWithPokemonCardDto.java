package com.scoutingtcg.purchases.cardforsale.dto;

import com.scoutingtcg.purchases.cardforsale.model.CardForSale;
import com.scoutingtcg.purchases.pokemoncard.model.PokemonCard;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CardForSaleWithPokemonCardDto {
    private CardForSale cardForSale;
    private PokemonCard pokemonCard;
}
