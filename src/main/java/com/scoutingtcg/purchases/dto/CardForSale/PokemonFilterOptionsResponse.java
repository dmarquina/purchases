package com.scoutingtcg.purchases.dto.CardForSale;

import com.scoutingtcg.purchases.model.SetOption;

import java.util.List;

public record PokemonFilterOptionsResponse(
        List<SetOption> sets,
        List<String> conditions,
        List<String> printings
) {}
