package com.scoutingtcg.purchases.repository;

import com.scoutingtcg.purchases.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository
        extends JpaRepository<Product, Long> {

    List<Product> findTop5ByFranchiseAndPresentation(String franchise, String presentation);

    List<Product> findTop5ByFranchiseAndPresentationNot(String franchise, String presentation);

    Page<Product> findByFranchiseAndPresentation(String franchise, String presentation, Pageable pageable);

    Page<Product> findByFranchiseAndPresentationNot(String franchise, String presentation, Pageable pageable);

}
