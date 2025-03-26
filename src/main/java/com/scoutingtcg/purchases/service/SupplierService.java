package com.scoutingtcg.purchases.service;

import com.scoutingtcg.purchases.model.Supplier;
import com.scoutingtcg.purchases.repository.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SupplierService {

  private final SupplierRepository supplierRepository;

  public SupplierService(SupplierRepository supplierRepository) {
    this.supplierRepository = supplierRepository;
  }

  public List<Supplier> getAllSuppliers() {
    return supplierRepository.findAll();
  }

  public Optional<Supplier> getSupplierById(Long id) {
    return supplierRepository.findById(id);
  }

  public Supplier saveOrUpdateSupplier(Supplier supplier) {
    return supplierRepository.save(supplier);
  }

  public Supplier updateSupplier(Supplier supplier) {
    return supplierRepository.save(supplier);
  }

  public void deleteSupplier(Long id) {
    supplierRepository.deleteById(id);
  }
}
