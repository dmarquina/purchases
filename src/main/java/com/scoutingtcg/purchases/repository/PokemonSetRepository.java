package com.scoutingtcg.purchases.repository;

import com.scoutingtcg.purchases.model.PokemonSet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PokemonSetRepository extends JpaRepository<PokemonSet, String> {

    List<PokemonSet> findAllByReleaseDateAfterOrderByReleaseDateDesc(LocalDate date);
}
