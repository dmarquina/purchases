package com.scoutingtcg.purchases.repository;

import com.scoutingtcg.purchases.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository
        extends JpaRepository<Product, Long> {


    @Query("""
                SELECT DISTINCT c.cardId
                FROM CardForSale c
                JOIN PokemonCard p ON c.cardId = p.id
                WHERE c.franchise = 'POKEMON'
                ORDER BY p.name ASC
            """)
    List<String> findTop5CardIdsByFranchisePokemon(Pageable limitFive);

    List<Product> findTop5ByFranchiseAndPresentationNot(String franchise, String presentation);

    Page<Product> findByFranchiseAndPresentation(String franchise, String presentation, Pageable pageable);

    Page<Product> findByFranchiseAndPresentationNot(String franchise, String presentation, Pageable pageable);

}
