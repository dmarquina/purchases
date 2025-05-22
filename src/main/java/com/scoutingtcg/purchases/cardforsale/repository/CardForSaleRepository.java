package com.scoutingtcg.purchases.cardforsale.repository;

import com.scoutingtcg.purchases.cardforsale.dto.CardForSaleWithDetailsDto;
import com.scoutingtcg.purchases.cardforsale.dto.CardForSaleWithPokemonCardDto;
import com.scoutingtcg.purchases.cardforsale.model.CardForSale;
import com.scoutingtcg.purchases.shared.model.Franchise;
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


    @Query("""
                SELECT new com.scoutingtcg.purchases.cardforsale.dto.CardForSaleWithPokemonCardDto(c, p)
                FROM CardForSale c
                JOIN PokemonCard p ON c.cardId = p.id
                WHERE c.cardId IN :cardIds
                AND c.status = 'ACTIVE'
            """)
    List<CardForSaleWithPokemonCardDto> findByCardIdIn(@Param("cardIds") List<String> cardIds);


    @Query("""
                SELECT new com.scoutingtcg.purchases.cardforsale.dto.CardForSaleWithPokemonCardDto(c, p)
                FROM CardForSale c
                JOIN PokemonCard p ON c.cardId = p.id
                WHERE c.franchise = 'POKEMON'
            """)
    List<CardForSaleWithPokemonCardDto> findAllCardForSaleWithPokemonCard(
    );

    @Query("""
                SELECT new com.scoutingtcg.purchases.cardforsale.dto.CardForSaleWithPokemonCardDto(c, p)
                FROM CardForSale c
                JOIN PokemonCard p ON c.cardId = p.id
                WHERE c.franchise = 'POKEMON'
                AND c.status = 'PENDING'
                AND (c.price IS NULL OR c.price = 0)
            """)
    List<CardForSaleWithPokemonCardDto> findNoPriceAndPendingCardForSaleWithPokemonCard(
    );

    //TODO: Quitar el primer where
    @Query("""
                    SELECT c
                    FROM CardForSale c
                    WHERE c.price IS NOT NULL
                    AND (c.price IS NULL OR c.price = 0)
                    AND c.status = 'PENDING'
            """)
    List<CardForSale> findByPriceGreaterThanZeroAndStatusPending();

    @Query("""
            SELECT new com.scoutingtcg.purchases.cardforsale.dto.CardForSaleWithDetailsDto(
                csf.id, csf.printing, pc.name, pc.number, pc.rarity, pc.setName)
            FROM CardForSale csf
            JOIN PokemonCard pc ON pc.id = csf.cardId
            WHERE csf.id = :id
        """)
    Optional<CardForSaleWithDetailsDto> findCardForSaleDetailsById(@Param("id") Long id);

}
