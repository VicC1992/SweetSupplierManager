package com.mybakery.sweet_suppliers.controller;

import com.mybakery.sweet_suppliers.dto.OrderItemRequest;
import com.mybakery.sweet_suppliers.dto.OrderRequest;
import com.mybakery.sweet_suppliers.entity.Order;
import com.mybakery.sweet_suppliers.entity.OrderItem;
import com.mybakery.sweet_suppliers.entity.SupplierProduct;
import com.mybakery.sweet_suppliers.repository.OrderItemRepository;
import com.mybakery.sweet_suppliers.service.OrderItemService;
import com.mybakery.sweet_suppliers.service.OrderService;
import com.mybakery.sweet_suppliers.service.SupplierProductService;
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
import java.util.List;

@Controller
@RequestMapping("orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

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
        orderItem.setUnitOfMeasure(supplierProduct.getUnitOfMeasure());
        orderItemRepository.save(orderItem);
        return "redirect:/orders/" + orderId + "/details";
    }

    @GetMapping("/select-order-to-add-product/{productId}")
    public String showOrdersForProduct(@PathVariable Long productId, Model model) {
        List<Order> orders = orderService.getAllOrders();
        if (orders.isEmpty()) {
            throw new IllegalStateException("No orders found.");
        }
        System.out.println("Produc ID:" + productId);
        orders.forEach(order -> System.out.println("Order ID:" + order.getId()));
        model.addAttribute("orders", orders);
        model.addAttribute("productId", productId);
        return "select_order";
    }

    @PostMapping("/{orderId}/add-product-direct/{productId}")
    public String addProductToOrderDirect(@PathVariable Long orderId, @PathVariable Long productId) {
        Order order = orderService.getOrderById(orderId)
                .orElseThrow(()-> new IllegalArgumentException("Order not found with Id:" + orderId));
        SupplierProduct supplierProduct = supplierProductService.findById(productId)
                .orElseThrow(()-> new IllegalArgumentException("Product not found with ID:" + productId));
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(supplierProduct.getProduct());
        orderItem.setQuantity(1);
        orderItem.setUnitOfMeasure(supplierProduct.getUnitOfMeasure());
        orderItem.setPrice(supplierProduct.getPrice());
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
}
