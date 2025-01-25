package com.mybakery.sweet_suppliers.service;

import com.mybakery.sweet_suppliers.Enums.OrderStatus;
import com.mybakery.sweet_suppliers.entity.Order;
import com.mybakery.sweet_suppliers.entity.OrderItem;
import com.mybakery.sweet_suppliers.entity.Product;
import com.mybakery.sweet_suppliers.entity.SupplierProduct;
import com.mybakery.sweet_suppliers.repository.OrderRepository;
import com.mybakery.sweet_suppliers.repository.SupplierProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    private SupplierProductRepository supplierProductRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(String name, OrderStatus status) {
        Order order = new Order();
        order.setName(name);
        order.setStatus(status);
        order.setOrderDate(LocalDateTime.now());
        return orderRepository.save(order);
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order>getAllOrders() {
        return orderRepository.findAll();
    }

    public void deleteOrderById(Long id) {
        orderRepository.deleteById(id);
    }

    public void addProductToOrder(Long orderId, Long productId, int quantity) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        SupplierProduct supplierProduct = supplierProductRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        Product product = supplierProduct.getProduct();

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(quantity);
        orderItem.setUnitOfMeasure(supplierProduct.getUnitOfMeasure());
        order.addOrderItem(orderItem);
        orderRepository.save(order);
    }

    public List<Order>getOrdersByDate() {
        LocalDate today = LocalDate.now();
        return orderRepository.findByOrderDateBetween(
                today.atStartOfDay(), today.plusDays(1).atStartOfDay()
        );
    }
}
