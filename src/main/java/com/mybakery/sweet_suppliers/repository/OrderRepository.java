package com.mybakery.sweet_suppliers.repository;

import com.mybakery.sweet_suppliers.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
