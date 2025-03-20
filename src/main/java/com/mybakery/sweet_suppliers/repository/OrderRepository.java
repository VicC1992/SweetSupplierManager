package com.mybakery.sweet_suppliers.repository;

import com.mybakery.sweet_suppliers.Enums.OrderStatus;
import com.mybakery.sweet_suppliers.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o ORDER BY o.orderDate ASC")
    List<Order> findAllOrderByDate();


    List<Order> findByOrderDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
    List<Order>findBySupplierId(Long supplierId);
    List<Order>findByStatus(OrderStatus status);
}
