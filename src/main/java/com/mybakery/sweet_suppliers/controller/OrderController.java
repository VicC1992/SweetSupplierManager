package com.mybakery.sweet_suppliers.controller;

import com.mybakery.sweet_suppliers.Enums.OrderStatus;
import com.mybakery.sweet_suppliers.dto.OrderItemRequest;
import com.mybakery.sweet_suppliers.dto.OrderRequest;
import com.mybakery.sweet_suppliers.entity.Order;
import com.mybakery.sweet_suppliers.entity.OrderItem;
import com.mybakery.sweet_suppliers.entity.SupplierProduct;
import com.mybakery.sweet_suppliers.repository.OrderItemRepository;
import com.mybakery.sweet_suppliers.repository.SupplierProductRepository;
import com.mybakery.sweet_suppliers.service.OrderItemService;
import com.mybakery.sweet_suppliers.service.OrderService;
import com.mybakery.sweet_suppliers.service.SupplierProductService;
import com.mybakery.sweet_suppliers.service.SupplierService;
import com.mybakery.sweet_suppliers.util.PdfGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private SupplierProductService supplierProductService;

    @Autowired
    private SupplierProductRepository supplierProductRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @GetMapping("/create")
    public String showCreateOrderForm(Model model) {
        model.addAttribute("order", new OrderRequest());
        model.addAttribute("suppliers", supplierService.getActiveSuppliers());
        return "create_order_form";
    }

    @PostMapping("/create")
    public String createOrder(@ModelAttribute OrderRequest orderRequest) {
        orderService.createOrder(orderRequest.getName(), OrderStatus.InProcess, orderRequest.getSupplierId());
        return "redirect:/orders/see-all";
    }

    @GetMapping("/create-order-toReturn")
    public String showCreateToReturnOrderForm(Model model) {
        model.addAttribute("order", new OrderRequest());
        model.addAttribute("suppliers", supplierService.getActiveSuppliers());
        return "create_to_return_order_form";
    }

    @PostMapping("/create-order-toReturn")
    public String createToReturnOrder(@ModelAttribute OrderRequest orderRequest) {
        orderService.createOrder(orderRequest.getName(), OrderStatus.Return, orderRequest.getSupplierId());
        return "redirect:/procurement-manager/orders-to-return";
    }

    @GetMapping("/{orderId}/add-product")
    public String showAddProductForm(@PathVariable Long orderId, Model model) {
        Order order = orderService.getOrderById(orderId)
                .orElseThrow(()-> new IllegalArgumentException("Order not found with ID: " + orderId));
        Long supplierId = order.getSupplier().getId();

        List<SupplierProduct> supplierProducts = supplierProductService.findBySupplierId(supplierId);
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
        orderItem.setUnitOfMeasure(supplierProduct.getUnitOfMeasure());
        orderItem.setPrice(supplierProduct.getPrice());
        orderItemRepository.save(orderItem);
        return "redirect:/orders/" + orderId + "/details";
    }

    @GetMapping("/select-order-to-add-product/{supplierProductId}")
    public String showOrdersForProduct(@PathVariable Long supplierProductId, Model model) {
        SupplierProduct supplierProduct = supplierProductService.findById(supplierProductId)
                .orElseThrow(()-> new IllegalArgumentException("Product not found with ID:" + supplierProductId));
        List<Order> orders = orderService.getOrdersBySupplierId(supplierProduct.getSupplier().getId());
        if (orders.isEmpty()) {
            throw new IllegalStateException("No orders found.");
        }
        model.addAttribute("orders", orders);
        model.addAttribute("supplierProductId", supplierProductId);
        return "select_order";
    }

    @PostMapping("/{orderId}/add-product-direct/{supplierProductId}")
    public String addProductToOrderDirect(@PathVariable Long orderId, @PathVariable Long supplierProductId) {
        Order order = orderService.getOrderById(orderId)
                .orElseThrow(()-> new IllegalArgumentException("Order not found with Id:" + orderId));
        SupplierProduct supplierProduct = supplierProductService.findById(supplierProductId)
                .orElseThrow(()-> new IllegalArgumentException("Product not found with ID:" + supplierProductId));
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(supplierProduct.getProduct());
        orderItem.setQuantity(1);
        orderItem.setUnitOfMeasure(supplierProduct.getUnitOfMeasure());
        orderItem.setPrice(supplierProduct.getPrice());
        orderItemRepository.save(orderItem);
        return "redirect:/orders/" + orderId + "/details";
    }

    @GetMapping("/see-all")
    public String viewAllOrders(@RequestParam(value = "status", required = false) OrderStatus status, Model model) {
        List<Order> orders;
        if (status == null) {
            orders = orderService.getAllOrdersSorted();
        } else {
            orders = orderService.getOrdersByStatus(status).stream()
                    .sorted(Comparator.comparing(Order::getOrderDate))
                    .collect(Collectors.toList());
        }
        model.addAttribute("orders", orders);
        model.addAttribute("orderStatuses", OrderStatus.values());
        model.addAttribute("selectedStatus", status);
        return "orders_list";
    }

    @GetMapping("/received")
    public String viewReceivedOrders(Model model) {
        List<Order> orders = orderService.getOrdersByStatus(OrderStatus.Received);
        model.addAttribute("orders", orders);
        return "received_orders";
    }

    @GetMapping("/to-receive")
    public String viewToReceivedOrders(Model model) {
        List<Order> orders = orderService.getOrdersByStatus(OrderStatus.ToReceive);
        model.addAttribute("orders", orders);
        return "to_receive_orders";
    }

    @GetMapping("/{orderId}/details")
    public String viewOrderDetails(@PathVariable Long orderId,@RequestParam(defaultValue = "detailed") String viewType, Model model) {
        Order order = orderService.getOrderById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        List<OrderItem> sortedOrderItems = order.getOrderItems().stream()
                .sorted(Comparator.comparing(item -> item.getProduct().getName()))
                .collect(Collectors.toList());

        model.addAttribute("order", order);
        model.addAttribute("orderItems", sortedOrderItems);
        model.addAttribute("orderStatuses", OrderStatus.values());
        model.addAttribute("viewType", viewType);
        return "order_details";
    }

    @PostMapping("/{orderId}/update-status")
    public String updateOrderStatus(@PathVariable Long orderId, @RequestParam OrderStatus status) {
        Order order = orderService.getOrderById(orderId)
                .orElseThrow(()-> new IllegalArgumentException("Order not found with ID:" + orderId));
        order.setStatus(status);
        orderService.saveOrder(order);
        return "redirect:/orders/{orderId}/details";
    }

    @PostMapping("/{orderId}/set-status")
    public String setReceivedStatus(@PathVariable Long orderId, @RequestParam OrderStatus status) {
        Order order = orderService.getOrderById(orderId)
                .orElseThrow(()-> new IllegalArgumentException("Order not found with ID:" + orderId));
        order.setStatus(OrderStatus.Received);
        orderService.saveOrder(order);
        return "redirect:/orders/to-receive";
    }

    @PostMapping("/{orderId}/update-quantity/{itemId}")
    public String updateProductQuantity(@PathVariable Long orderId,
                                        @PathVariable Long itemId,
                                        @RequestParam int quantity) {
        OrderItem orderItem = orderItemRepository.findById(itemId)
                .orElseThrow(()-> new IllegalArgumentException("Order Item not found with ID:" + itemId));
        if (quantity <= 0) {
            throw new IllegalStateException("Quantity must be greater than zero.");
        }
        orderItem.setQuantity(quantity);
        orderItemRepository.save(orderItem);
        return "redirect:/orders/" + orderId + "/details";
    }

    @PostMapping("/{orderId}/remove-product/{itemId}")
    public String removeProductFromOrder(@PathVariable Long orderId, @PathVariable Long itemId) {
        OrderItem orderItem = orderItemRepository.findById(itemId)
                .orElseThrow(()-> new IllegalArgumentException("OrderItem not found with ID:" + itemId));
        orderItemRepository.delete(orderItem);
        return "redirect:/orders/" + orderId + "/details";
    }

    @PostMapping("/{orderId}/update-remark/{itemId}")
    public String updateOrderItemRemark(@PathVariable Long orderId, @PathVariable Long itemId, @RequestParam String remark) {
        orderItemService.updateRemark(itemId, remark);
        return "redirect:/orders/" + orderId + "/details?viewType=summary";
    }

    @GetMapping("/{orderId}/download-pdf")
    public ResponseEntity<byte[]>downloaderPdf(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderId(orderId);

        PdfGenerator pdfGenerator = new PdfGenerator();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        pdfGenerator.generateOrderPdf(outputStream, order, orderItems);

        byte[] pdfContent = outputStream.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Order_" + orderId + ".pdf");

        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }
    @GetMapping("/to-return")
    public String viewToRetunrOrders(Model model) {
        List<Order> orders = orderService.getOrdersByStatus(OrderStatus.Return);
        model.addAttribute("orders", orders);
        return "orders_to_return";
    }

    @GetMapping("/to-receive/warehouse-manager")
    public String showOrdersToReceive(Model model) {
        model.addAttribute("orders", orderService.getOrdersByDate());
        return "pending_orders";
    }

    @GetMapping("/to-receive/{orderId}/details")
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
