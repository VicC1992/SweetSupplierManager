package com.mybakery.sweet_suppliers.controller;

import com.mybakery.sweet_suppliers.dto.OrderItemRequest;
import com.mybakery.sweet_suppliers.dto.OrderRequest;
import com.mybakery.sweet_suppliers.entity.Order;
import com.mybakery.sweet_suppliers.service.OrderService;
import com.mybakery.sweet_suppliers.service.SupplierProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private SupplierProductService supplierProductService;

    @GetMapping("/create")
    public String showCreateOrderForm(Model model) {
        model.addAttribute("order", new OrderRequest());
        return "create_order_form";
    }

    @PostMapping("/create")
    public String createOrder(@ModelAttribute OrderRequest orderRequest){
        orderService.createOrder(orderRequest.getName(), orderRequest.getStatus());
        return "redirect:/orders";
    }

    @GetMapping("/{orderId}/add-product")
    public String showAddProductForm(@PathVariable Long orderId, Model model) {
        model.addAttribute("orderId", orderId);
        model.addAttribute("products", supplierProductService.findAll());
        model.addAttribute("orderItem", new OrderItemRequest());
        return "add_product_to_order";
    }

    @PostMapping("/{orderId}/add-product")
    public String addProductToOrder(@PathVariable Long orderId, @ModelAttribute OrderItemRequest orderItemRequest) {
        orderService.addProductToOrder(orderId, orderItemRequest.getProductId(), orderItemRequest.getQuantity());
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
