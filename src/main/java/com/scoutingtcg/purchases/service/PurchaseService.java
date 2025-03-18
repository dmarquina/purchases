package com.scoutingtcg.purchases.service;

import com.scoutingtcg.purchases.model.Purchase;
import com.scoutingtcg.purchases.repository.PurchaseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PurchaseService {

  private final PurchaseRepository purchaseRepository;

  public PurchaseService(PurchaseRepository purchaseRepository) {
    this.purchaseRepository = purchaseRepository;
  }

  public List<Purchase> getAllPurchases() {
    return purchaseRepository.findAll();
  }

  public Optional<Purchase> getPurchaseById(Long id) {
    return purchaseRepository.findById(id);
  }

  public Purchase savePurchase(Purchase purchase) {
    return purchaseRepository.save(purchase);
  }

  public void deletePurchase(Long id) {
    purchaseRepository.deleteById(id);
  }
}
