package com.scoutingtcg.purchases.supplier.controller;

import com.scoutingtcg.purchases.supplier.service.SupplierService;
import com.scoutingtcg.purchases.supplier.model.Supplier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {

  private final SupplierService supplierService;

  public SupplierController(SupplierService supplierService) {
    this.supplierService = supplierService;
  }

  @GetMapping
  public List<Supplier> getAllSuppliers() {
    return supplierService.getAllSuppliers();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Supplier> getSupplierById(@PathVariable Long id) {
    return supplierService.getSupplierById(id)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public Supplier createSupplier(@RequestBody Supplier supplier) {
    return supplierService.saveOrUpdateSupplier(supplier);
  }

  @PutMapping
  public Supplier updateSupplier(@RequestBody Supplier supplier) {
    return supplierService.saveOrUpdateSupplier(supplier);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
    supplierService.deleteSupplier(id);
    return ResponseEntity.noContent().build();
  }
}
