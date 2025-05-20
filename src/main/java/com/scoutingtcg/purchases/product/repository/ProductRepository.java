package com.scoutingtcg.purchases.product.repository;

import com.scoutingtcg.purchases.product.model.Product;
import com.scoutingtcg.purchases.shared.model.Franchise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository
        extends JpaRepository<Product, Long> {

    List<Product> findTop5ByFranchiseAndPresentationNotAndStockGreaterThan(Franchise franchise, String presentation, int stock);

    Page<Product> findByFranchiseAndPresentationNotAndStockGreaterThan(Franchise franchise, String presentation, Pageable pageable, int stock);

}
