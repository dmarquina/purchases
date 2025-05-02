package com.scoutingtcg.purchases.repository;

import com.scoutingtcg.purchases.dto.CardForSale.CardForSaleWithPokemonCardDto;
import com.scoutingtcg.purchases.model.CardForSale;
import com.scoutingtcg.purchases.model.Franchise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CardForSaleRepository extends JpaRepository<CardForSale, Long> {


    Optional<CardForSale> findByCardIdAndCardConditionAndPrintingAndFranchise(
            String cardId, String cardCondition, String printing, Franchise franchise);

    @Query("""
    SELECT new com.scoutingtcg.purchases.dto.CardForSale.CardForSaleWithPokemonCardDto(c, p)
    FROM CardForSale c
    JOIN PokemonCard p ON c.cardId = p.id
    """)
    List<CardForSaleWithPokemonCardDto> findWithPokemonCard(Pageable pageable);

    @Query("SELECT COUNT(c) FROM CardForSale c")
    long countAllWithPokemonCard();


}
