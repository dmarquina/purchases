package com.scoutingtcg.purchases.controller;

import com.scoutingtcg.purchases.model.Purchase;
import com.scoutingtcg.purchases.service.PurchaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/purchases")
public class PurchaseController {

  private final PurchaseService purchaseService;

  public PurchaseController(PurchaseService purchaseService) {
    this.purchaseService = purchaseService;
  }

  @GetMapping
  public List<Purchase> getAllPurchases() {
    return purchaseService.getAllPurchases();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Purchase> getPurchaseById(@PathVariable Long id) {
    return purchaseService.getPurchaseById(id)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public Purchase createPurchase(@RequestBody Purchase purchase) {
    return purchaseService.savePurchase(purchase);
  }

  @PutMapping
  public Purchase updatePurchase(@RequestBody Purchase purchase) {
    return purchaseService.updatePurchase(purchase);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePurchase(@PathVariable Long id) {
    purchaseService.deletePurchase(id);
    return ResponseEntity.noContent().build();
  }
}
