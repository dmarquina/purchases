package com.scoutingtcg.purchases.pokemoncard.repository;

import com.scoutingtcg.purchases.pokemoncard.model.PokemonCardMarketPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PokemonCardMarketPriceRepository extends JpaRepository<PokemonCardMarketPrice, String> {
}
