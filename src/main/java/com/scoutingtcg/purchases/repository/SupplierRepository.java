package com.scoutingtcg.purchases.repository;

import com.scoutingtcg.purchases.model.Purchase;
import com.scoutingtcg.purchases.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SupplierRepository
  extends JpaRepository<Supplier, Long> {

}
