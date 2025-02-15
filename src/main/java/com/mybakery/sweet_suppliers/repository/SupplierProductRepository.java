package com.mybakery.sweet_suppliers.repository;

import com.mybakery.sweet_suppliers.entity.SupplierProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SupplierProductRepository extends JpaRepository<SupplierProduct, Long> {
    Optional<SupplierProduct> findByProductId(Long productId);
    List<SupplierProduct> findByProduct_NameIgnoreCase(String productName);
    List<SupplierProduct>findBySupplierId(Long supplierId);
}
