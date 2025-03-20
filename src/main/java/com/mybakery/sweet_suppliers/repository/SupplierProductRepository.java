package com.mybakery.sweet_suppliers.repository;

import com.mybakery.sweet_suppliers.entity.SupplierProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SupplierProductRepository extends JpaRepository<SupplierProduct, Long> {
    Optional<SupplierProduct> findByProductId(Long productId);
    List<SupplierProduct> findByProduct_NameIgnoreCase(String productName);
    List<SupplierProduct>findBySupplierId(Long supplierId);

    @Query("SELECT sp FROM SupplierProduct sp WHERE sp.supplier.id = :supplierId ORDER BY sp.product.name ASC")
    List<SupplierProduct> findBySupplierIdOrderByProductName(@Param("supplierId") Long supplierId);
}
