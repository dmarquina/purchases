package com.scoutingtcg.purchases.repository;

import com.scoutingtcg.purchases.model.PokemonCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PokemonCardRepository extends JpaRepository<PokemonCard, String> {


}
