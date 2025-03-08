package com.mybakery.sweet_suppliers.service;

import com.mybakery.sweet_suppliers.Enums.UnitOfMeasure;
import com.mybakery.sweet_suppliers.entity.Order;
import com.mybakery.sweet_suppliers.entity.OrderItem;
import com.mybakery.sweet_suppliers.entity.Product;
import com.mybakery.sweet_suppliers.repository.OrderItemRepository;
import com.mybakery.sweet_suppliers.repository.OrderRepository;
import com.mybakery.sweet_suppliers.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderItemService(OrderItemRepository orderItemRepository, OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public OrderItem addProductToOrder(Long orderId, Long productId, Integer quantity, UnitOfMeasure unitOfMeasure) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new IllegalArgumentException("Order not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not founfd"));
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(quantity);
        orderItem.setUnitOfMeasure(unitOfMeasure);

        return orderItemRepository.save(orderItem);
    }

    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    public void removeOrderItem(Long orderItemId) {
        orderItemRepository.deleteById(orderItemId);
    }

    public void updateRemark(Long itemId, String remark) {
        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(()-> new RuntimeException("Order Item not found"));
        item.setRemark(remark);
        orderItemRepository.save(item);
        updateOrderIssueStatus(item.getOrder());
    }

    private void updateOrderIssueStatus(Order order) {
        boolean issue = order.getOrderItems().stream()
                .anyMatch(item -> item.getRemark() != null && !item.getRemark().isEmpty());
        order.setIssue(issue);
        orderRepository.save(order);
    }
}
