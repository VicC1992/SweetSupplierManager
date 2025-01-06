package com.mybakery.sweet_suppliers.controller;

import com.mybakery.sweet_suppliers.dto.OrderItemRequest;
import com.mybakery.sweet_suppliers.dto.OrderRequest;
import com.mybakery.sweet_suppliers.entity.Order;
import com.mybakery.sweet_suppliers.entity.OrderItem;
import com.mybakery.sweet_suppliers.entity.SupplierProduct;
import com.mybakery.sweet_suppliers.repository.OrderItemRepository;
import com.mybakery.sweet_suppliers.service.OrderService;
import com.mybakery.sweet_suppliers.service.SupplierProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private SupplierProductService supplierProductService;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @GetMapping("/create")
    public String showCreateOrderForm(Model model) {
        model.addAttribute("order", new OrderRequest());
        return "create_order_form";
    }

    @PostMapping("/create")
    public String createOrder(@ModelAttribute OrderRequest orderRequest){
        orderService.createOrder(orderRequest.getName(), orderRequest.getStatus());
        return "redirect:/orders/see-all";
    }

    @GetMapping("/{orderId}/add-product")
    public String showAddProductForm(@PathVariable Long orderId, Model model) {
        List<SupplierProduct> supplierProducts = supplierProductService.findAll();
        model.addAttribute("orderId", orderId);
        model.addAttribute("supplierProducts", supplierProducts);
        model.addAttribute("orderItemRequest", new OrderItemRequest());
        return "add_product_to_order";
    }

    @PostMapping("/{orderId}/add-product")
    public String addProductToOrder(@PathVariable Long orderId,
                                    @ModelAttribute OrderItemRequest orderItemRequest) {

        Order order = orderService.getOrderById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));
        SupplierProduct supplierProduct = supplierProductService.findById(orderItemRequest.getProductId())
                .orElseThrow(()->new IllegalArgumentException("Product not found with ID:"));

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(supplierProduct.getProduct());
        orderItem.setQuantity(orderItemRequest.getQuantity());
        orderItem.setUnitOfMeasure(supplierProduct.getProduct().getUnitOfMeasure());
        orderItemRepository.save(orderItem);
        return "redirect:/orders/" + orderId + "/details";
    }

    @GetMapping("see-all")
    public String viewALlOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "orders_list";
    }

    @GetMapping("/{orderId}/details")
    public String viewOrderDetails(@PathVariable Long orderId, Model model) {
        Order order = orderService.getOrderById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        model.addAttribute("order", order);
        model.addAttribute("orderItems", order.getOrderItems());
        return "order_details";
    }
}
