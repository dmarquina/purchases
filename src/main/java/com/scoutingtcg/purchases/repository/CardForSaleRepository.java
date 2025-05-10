package com.scoutingtcg.purchases.repository;

import com.scoutingtcg.purchases.dto.CardForSale.CardForSaleWithPokemonCardDto;
import com.scoutingtcg.purchases.model.CardForSale;
import com.scoutingtcg.purchases.model.Franchise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CardForSaleRepository extends JpaRepository<CardForSale, Long> {


    Optional<CardForSale> findByCardIdAndCardConditionAndPrintingAndFranchise(
            String cardId, String cardCondition, String printing, Franchise franchise);


    @Query(
            value = """
                    SELECT DISTINCT c.cardId
                    FROM CardForSale c
                    JOIN PokemonCard p ON c.cardId = p.id
                    WHERE c.franchise = 'POKEMON'
                    AND c.status = 'ACTIVE'
                    AND (:sets IS NULL OR p.setId IN :sets)
                    AND (:conditions IS NULL OR c.cardCondition IN :conditions)
                    AND (:printings IS NULL OR c.printing IN :printings)
                    AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
                    """,
            countQuery = """
                    SELECT COUNT(DISTINCT c.cardId)
                    FROM CardForSale c
                    JOIN PokemonCard p ON c.cardId = p.id
                    WHERE c.franchise = 'POKEMON'
                    AND c.status = 'ACTIVE'
                    AND (:sets IS NULL OR p.setId IN :sets)
                    AND (:conditions IS NULL OR c.cardCondition IN :conditions)
                    AND (:printings IS NULL OR c.printing IN :printings)
                    AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
                    """
    )
    Page<String> findFilteredCardIdsWithPagination(
            @Param("sets") List<String> sets,
            @Param("conditions") List<String> conditions,
            @Param("printings") List<String> printings,
            @Param("name") String name,
            Pageable pageable
    );

    @Query("""
                SELECT c.cardId
                FROM CardForSale c
                JOIN PokemonCard p ON c.cardId = p.id
                WHERE c.franchise = 'POKEMON'
                AND c.status = 'ACTIVE'
                GROUP BY c.cardId
                ORDER BY MAX(c.id) DESC
            """)
    List<String> findTop4CardIdsByFranchisePokemon(Pageable pageable);


    // Para obtener los datos DTO luego de filtrar IDs
    @Query("""
                SELECT new com.scoutingtcg.purchases.dto.CardForSale.CardForSaleWithPokemonCardDto(c, p)
                FROM CardForSale c
                JOIN PokemonCard p ON c.cardId = p.id
                WHERE c.cardId IN :cardIds
                AND c.status = 'ACTIVE'
            """)
    List<CardForSaleWithPokemonCardDto> findByCardIdIn(@Param("cardIds") List<String> cardIds);


    @Query("""
                SELECT new com.scoutingtcg.purchases.dto.CardForSale.CardForSaleWithPokemonCardDto(c, p)
                FROM CardForSale c
                JOIN PokemonCard p ON c.cardId = p.id
                WHERE c.franchise = 'POKEMON'
            """)
    List<CardForSaleWithPokemonCardDto> findAllCardForSaleWithPokemonCard(
    );

    @Query("""
                SELECT new com.scoutingtcg.purchases.dto.CardForSale.CardForSaleWithPokemonCardDto(c, p)
                FROM CardForSale c
                JOIN PokemonCard p ON c.cardId = p.id
                WHERE c.franchise = 'POKEMON'
                AND c.status = 'PENDING'
                AND (c.price IS NULL OR c.price = 0)
            """)
    List<CardForSaleWithPokemonCardDto> findNoPriceAndPendingCardForSaleWithPokemonCard(
    );

    @Query("""
                    SELECT c
                    FROM CardForSale c
                    WHERE c.price IS NOT NULL
                    AND (c.price IS NULL OR c.price = 0)
                    AND c.status = 'PENDING'
            """)
    List<CardForSale> findByPriceGreaterThanZeroAndStatusPending();


}
