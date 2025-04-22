package com.scoutingtcg.purchases.repository;

import com.scoutingtcg.purchases.model.Product;
import com.scoutingtcg.purchases.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository
  extends JpaRepository<ProductImage, Long>
{
    void deleteAllByProduct(Product product);

}