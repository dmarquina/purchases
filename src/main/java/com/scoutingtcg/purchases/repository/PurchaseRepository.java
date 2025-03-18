package com.scoutingtcg.purchases.repository;

import com.scoutingtcg.purchases.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

}
