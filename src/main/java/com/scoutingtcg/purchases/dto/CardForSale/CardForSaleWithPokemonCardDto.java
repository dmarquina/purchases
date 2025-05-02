package com.scoutingtcg.purchases.dto.CardForSale;

import com.scoutingtcg.purchases.model.CardForSale;
import com.scoutingtcg.purchases.model.PokemonCard;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CardForSaleWithPokemonCardDto {
    private CardForSale cardForSale;
    private PokemonCard pokemonCard;
}
