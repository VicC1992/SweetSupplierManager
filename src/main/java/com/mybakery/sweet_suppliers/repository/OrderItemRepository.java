package com.mybakery.sweet_suppliers.repository;

import com.mybakery.sweet_suppliers.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
