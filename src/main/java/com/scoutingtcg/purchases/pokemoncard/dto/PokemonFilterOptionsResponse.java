package com.scoutingtcg.purchases.pokemoncard.dto;

import com.scoutingtcg.purchases.shared.model.SetOption;

import java.util.List;

public record PokemonFilterOptionsResponse(
        List<SetOption> sets,
        List<String> conditions,
        List<String> printings
) {}
