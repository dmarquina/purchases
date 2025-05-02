package com.scoutingtcg.purchases.service;

import com.scoutingtcg.purchases.model.PokemonSet;
import com.scoutingtcg.purchases.repository.PokemonSetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.scoutingtcg.purchases.util.Constants.COSMIC_ECLIPSE_RELEASE;

@Service
public class PokemonSetService {

    PokemonSetRepository pokemonSetRepository;

    public PokemonSetService(PokemonSetRepository pokemonSetRepository) {
        this.pokemonSetRepository = pokemonSetRepository;
    }

    public List<PokemonSet> getAllSets() {

        return pokemonSetRepository.findAllByReleaseDateAfterOrderByReleaseDateDesc(COSMIC_ECLIPSE_RELEASE.minusDays(1));
    }

}
