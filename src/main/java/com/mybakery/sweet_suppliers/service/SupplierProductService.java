package com.mybakery.sweet_suppliers.service;

import com.mybakery.sweet_suppliers.entity.SupplierProduct;
import com.mybakery.sweet_suppliers.repository.SupplierProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SupplierProductService {

    @Autowired
    private SupplierProductRepository supplierProductRepository;

    public Optional<SupplierProduct> findById(Long id) {
        return supplierProductRepository.findById(id);
    }

    public SupplierProduct save(SupplierProduct supplierProduct) {
        return supplierProductRepository.save(supplierProduct);
    }

    public void deleteById(Long productId) {
        supplierProductRepository.deleteById(productId);
    }

    public List<SupplierProduct> findAll() {
        return supplierProductRepository.findAll();
    }
}
