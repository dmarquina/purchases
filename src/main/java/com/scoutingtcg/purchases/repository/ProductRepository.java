package com.scoutingtcg.purchases.repository;

import com.scoutingtcg.purchases.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository
        extends JpaRepository<Product, Long> {

    List<Product> findTop5ByFranchiseAndPresentationNotAndStockGreaterThan(String franchise, String presentation, int stock);

    Page<Product> findByFranchiseAndPresentationNotAndStockGreaterThan(String franchise, String presentation, Pageable pageable, int stock);

}
