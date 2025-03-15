package com.mybakery.sweet_suppliers.controller;

import com.mybakery.sweet_suppliers.Enums.OrderStatus;
import com.mybakery.sweet_suppliers.entity.Order;
import com.mybakery.sweet_suppliers.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/warehouse-manager")
public class WarehouseManagerController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/home-page")
    public String viewFirstPageWarehouseManager() {
        return "warehouse_manager_home_page";
    }

    @GetMapping("/orders-to-receive")
    public String showOrdersToReceive(Model model) {
        model.addAttribute("orders", orderService.getOrdersByDate());
        return "pending_orders";
    }

    @GetMapping("/order-to-receive/{orderId}/details")
    public String viewOrder(@PathVariable Long orderId, @RequestParam(defaultValue = "detailed") String viewType, Model model) {
        Order order = orderService.getOrderById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        model.addAttribute("order", order);
        model.addAttribute("orderItems", order.getOrderItems());
        model.addAttribute("orderStatuses", OrderStatus.values());
        model.addAttribute("viewType", viewType);
        return "order_details";
    }
}
