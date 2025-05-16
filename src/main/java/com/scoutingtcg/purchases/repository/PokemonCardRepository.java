package com.scoutingtcg.purchases.repository;

import com.scoutingtcg.purchases.model.pokemon.PokemonCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PokemonCardRepository extends JpaRepository<PokemonCard, String> {


}
