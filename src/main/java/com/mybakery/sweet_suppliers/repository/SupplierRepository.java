package com.mybakery.sweet_suppliers.repository;

import com.mybakery.sweet_suppliers.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}
