package com.mybakery.sweet_suppliers.repository;

import com.mybakery.sweet_suppliers.entity.RestockItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestockItemRepository extends JpaRepository<RestockItem, Long> {
}
