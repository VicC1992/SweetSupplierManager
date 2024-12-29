package com.mybakery.sweet_suppliers.repository;

import com.mybakery.sweet_suppliers.entity.SupplierProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierProductRepository extends JpaRepository<SupplierProduct, Long> {
}
