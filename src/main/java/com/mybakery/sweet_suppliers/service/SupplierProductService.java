package com.mybakery.sweet_suppliers.service;

import com.mybakery.sweet_suppliers.entity.SupplierProduct;
import com.mybakery.sweet_suppliers.repository.SupplierProductRepository;
import com.mybakery.sweet_suppliers.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SupplierProductService {

    @Autowired
    private SupplierProductRepository supplierProductRepository;

    public void save(SupplierProduct supplierProduct) {
        supplierProductRepository.save(supplierProduct);
    }
}
