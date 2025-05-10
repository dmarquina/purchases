package com.scoutingtcg.purchases.service;

import com.scoutingtcg.purchases.model.Product;
import com.scoutingtcg.purchases.model.Purchase;
import com.scoutingtcg.purchases.model.Supplier;
import com.scoutingtcg.purchases.repository.ProductRepository;
import com.scoutingtcg.purchases.repository.PurchaseRepository;
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
    //TODO: Mejorar performance, podrias solo manejar ids de productos, no objetos completos y de supplier
    product.setStock(product.getStock() + purchase.getQuantity());
    productRepository.save(product);
    return purchaseRepository.save(purchase);
  }


  public void updatePurchaseStatus(Long purchaseId) {
    Purchase purchase = purchaseRepository.findById(purchaseId)
            .orElseThrow(() -> new RuntimeException("Purchase not found"));

    if (purchase.getStatus() == null) {
      purchase.setStatus("PENDIENTE");
    } else if (purchase.getStatus().equals("PENDIENTE")) {
      purchase.setStatus("PAGADO");
    } else if (purchase.getStatus().equals("PAGADO")) {
      purchase.setStatus("RECIBIDO");
    } else {
      throw new RuntimeException("Invalid status");
    }
    purchaseRepository.save(purchase);
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
