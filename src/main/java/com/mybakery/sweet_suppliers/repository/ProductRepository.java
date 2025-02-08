package com.mybakery.sweet_suppliers.repository;

import com.mybakery.sweet_suppliers.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);
    @Query("SELECT DISTINCT p FROM Product p ORDER BY p.name ASC")
    List<Product> findAllDistinctByOrderByNameAsc();
}
