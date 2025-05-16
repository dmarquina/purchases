package com.scoutingtcg.purchases.purchase.repository;

import com.scoutingtcg.purchases.purchase.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

}
