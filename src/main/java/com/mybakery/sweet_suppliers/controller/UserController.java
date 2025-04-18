package com.mybakery.sweet_suppliers.controller;

import com.mybakery.sweet_suppliers.Enums.RoleName;
import com.mybakery.sweet_suppliers.entity.Order;
import com.mybakery.sweet_suppliers.entity.Role;
import com.mybakery.sweet_suppliers.entity.Supplier;
import com.mybakery.sweet_suppliers.entity.User;
import com.mybakery.sweet_suppliers.repository.RestockItemRepository;
import com.mybakery.sweet_suppliers.repository.SupplierRepository;
import com.mybakery.sweet_suppliers.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import com.mybakery.sweet_suppliers.repository.RoleRepository;
import com.mybakery.sweet_suppliers.repository.UserRepository;

import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RestockItemRepository restockItemRepository;

    @Autowired
    private OrderService orderService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "signup_form";
    }

    @PostMapping("/register/save")
    public String processRegister(User user) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        Role userRole = roleRepository.findByName(RoleName.ROLE_PROCUREMENT_MANAGER).orElseThrow(()-> new RuntimeException("Role PROCUREMENT_MANAGER not found"));
        user.setRole(userRole);
        userRepository.save(user);
        return "register_success";
    }
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> listUsers = userRepository.findAll();
        model.addAttribute("listUsers", listUsers);
        return "users";
    }

    @GetMapping("/procurement-manager/home-page")
    public String viewFirstPageProcurementManager(Model model) {
        long restockCount = restockItemRepository.count();
        model.addAttribute("restockCount", restockCount);
        return "procurement_manager_home_page";
    }

    @GetMapping("/warehouse-manager/home-page")
    public String viewFirstPageWarehouseManager(Model model) {
        List<Order>todayReturnOrders = orderService.getOrdersToSentToday();
        long returnOrderCount = todayReturnOrders.size();
        List<Order>todayPendingOrders = orderService.getOrdersToReceiveToday();
        long pendingOrderCount = todayPendingOrders.size();
        model.addAttribute("returnOrderCount", returnOrderCount);
        model.addAttribute("pendingOrderCount", pendingOrderCount);
        return "warehouse_manager_home_page";
    }
}
