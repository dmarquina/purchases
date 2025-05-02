package com.scoutingtcg.purchases.controller;

import com.scoutingtcg.purchases.model.PokemonSet;
import com.scoutingtcg.purchases.service.PokemonSetService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sets")
public class PokemonSetsController {
    private final PokemonSetService pokemonSetService;

    public PokemonSetsController(PokemonSetService pokemonSetService) {
        this.pokemonSetService = pokemonSetService;
    }

    @GetMapping
    public List<PokemonSet> getAllSets() {
        return pokemonSetService.getAllSets();
    }
}
