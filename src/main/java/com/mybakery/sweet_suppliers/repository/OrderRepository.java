package com.mybakery.sweet_suppliers.repository;

import com.mybakery.sweet_suppliers.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByOrderDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
    List<Order>findBySupplierId(Long supplierId);
}
