package com.scoutingtcg.purchases.service;

import com.scoutingtcg.purchases.model.PokemonCard;
import com.scoutingtcg.purchases.repository.PokemonCardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PokemonCardService {

    private final PokemonCardRepository pokemonCardRepository;

    public PokemonCardService(PokemonCardRepository pokemonCardRepository) {
        this.pokemonCardRepository = pokemonCardRepository;
    }


}
