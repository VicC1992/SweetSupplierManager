package com.mybakery.sweet_suppliers.repository;

import com.mybakery.sweet_suppliers.Enums.SupplierStatus;
import com.mybakery.sweet_suppliers.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    @Query("SELECT s FROM Supplier s WHERE s.status =:status ORDER BY s.name ASC")
    List<Supplier>findActiveSuppliersSortedByName(@Param("status") SupplierStatus status);
}
