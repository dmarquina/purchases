package com.scoutingtcg.purchases.supplier.repository;

import com.scoutingtcg.purchases.supplier.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SupplierRepository
  extends JpaRepository<Supplier, Long> {

}
