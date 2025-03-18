package com.scoutingtcg.purchases.repository;

import com.scoutingtcg.purchases.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository
  extends JpaRepository<Product, Long>
{

}