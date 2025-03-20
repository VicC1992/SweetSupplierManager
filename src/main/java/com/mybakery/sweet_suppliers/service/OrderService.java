package com.mybakery.sweet_suppliers.service;

import com.mybakery.sweet_suppliers.Enums.OrderStatus;
import com.mybakery.sweet_suppliers.entity.*;
import com.mybakery.sweet_suppliers.repository.OrderRepository;
import com.mybakery.sweet_suppliers.repository.SupplierProductRepository;
import com.mybakery.sweet_suppliers.repository.SupplierRepository;
import com.mybakery.sweet_suppliers.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
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

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private UserRepository userRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(String name, OrderStatus status, Long supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(()-> new IllegalArgumentException("Supplier not found with ID:" + supplierId));

        Order order = new Order();
        order.setName(name);
        order.setStatus(status != null ? status : OrderStatus.InProcess);
        order.setOrderDate(LocalDateTime.now());
        order.setSupplier(supplier);
        return orderRepository.save(order);
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public void saveOrder(Order order) {
        orderRepository.save(order);
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

    public List<Order> getAllOrdersSorted() {
        return orderRepository.findAllOrderByDate();
    }

    public List<Order> getOrdersBySupplierId(Long supplierId) {
        return orderRepository.findBySupplierId(supplierId);
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    public void receiveOrder(Long orderId, String email) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new EntityNotFoundException("Order not found with ID:" + orderId));

        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new EntityNotFoundException("User not found with email:" + email ));
        order.setStatus(OrderStatus.Received);
        order.setReceivedAt(LocalDateTime.now());
        order.setReceivedBy(user);

        orderRepository.save(order);
    }
}
