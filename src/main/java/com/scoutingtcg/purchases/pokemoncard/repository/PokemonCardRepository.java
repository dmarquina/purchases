package com.scoutingtcg.purchases.pokemoncard.repository;

import com.scoutingtcg.purchases.pokemoncard.model.PokemonCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PokemonCardRepository extends JpaRepository<PokemonCard, String> {


}
