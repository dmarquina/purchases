package com.scoutingtcg.purchases.purchase.service;

import com.scoutingtcg.purchases.product.model.Product;
import com.scoutingtcg.purchases.purchase.model.Purchase;
import com.scoutingtcg.purchases.purchase.repository.PurchaseRepository;
import com.scoutingtcg.purchases.product.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PurchaseService {

  private final PurchaseRepository purchaseRepository;
  private final ProductRepository productRepository;

  public List<Purchase> getAllPurchases() {
    return purchaseRepository.findAll();
  }

  public Optional<Purchase> getPurchaseById(Long id) {
    return purchaseRepository.findById(id);
  }

  public Purchase savePurchase(Purchase purchase) {
    Product product = productRepository.findById(purchase.getProduct().getProductId())
      .orElseThrow(() -> new RuntimeException("Product not found"));
    product.setStock(product.getStock() + purchase.getQuantity());
    productRepository.save(product);
    return purchaseRepository.save(purchase);
  }

  public Purchase updatePurchase(Purchase purchase) {
    Purchase oldPurchase = purchaseRepository.findById(purchase.getPurchaseId())
      .orElseThrow(() -> new RuntimeException("Purchase not found"));

    Product product = productRepository.findById(purchase.getProduct().getProductId())
      .orElseThrow(() -> new RuntimeException("Product not found"));

    Integer newStock = product.getStock() - oldPurchase.getQuantity() + purchase.getQuantity();
    product.setStock(newStock);
    productRepository.save(product);

    /*Supplier supplier = supplierRepository.findById(purchase.getSupplier().getSupplierId())
            .orElseThrow(() -> new RuntimeException("Supplier not found"));*/

    return purchaseRepository.save(purchase);
  }

  public void deletePurchase(Long id) {
    Purchase purchase = purchaseRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Purchase not found"));

    Product product = purchase.getProduct();

    product.setStock(product.getStock() - purchase.getQuantity());
    productRepository.save(product);
    purchaseRepository.deleteById(id);
  }
}
