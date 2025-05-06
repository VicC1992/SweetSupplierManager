package com.mybakery.sweet_suppliers.controller;

import com.mybakery.sweet_suppliers.entity.Order;
import com.mybakery.sweet_suppliers.entity.User;
import com.mybakery.sweet_suppliers.repository.RestockItemRepository;
import com.mybakery.sweet_suppliers.repository.SupplierRepository;
import com.mybakery.sweet_suppliers.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import com.mybakery.sweet_suppliers.repository.RoleRepository;
import com.mybakery.sweet_suppliers.repository.UserRepository;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
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
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OrderService orderService;

    @GetMapping("/procurement-manager/home")
    public String viewFirstPageProcurementManager(Model model) {
        long restockCount = restockItemRepository.count();
        model.addAttribute("restockCount", restockCount);
        return "procurement_manager_home_page";
    }

    @GetMapping("/warehouse-manager/home")
    public String viewFirstPageWarehouseManager(Model model) {
        List<Order>todayReturnOrders = orderService.getOrdersToSentToday();
        long returnOrderCount = todayReturnOrders.size();
        List<Order>todayPendingOrders = orderService.getOrdersToReceiveToday();
        long pendingOrderCount = todayPendingOrders.size();
        model.addAttribute("returnOrderCount", returnOrderCount);
        model.addAttribute("pendingOrderCount", pendingOrderCount);
        return "warehouse_manager_home_page";
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm() {
        return "change_password";
    }

    @PostMapping("change-password")
    public String processChangePassword(@RequestParam("newPassword") String newPassword, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(()-> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setMustChangePassword(false);
        userRepository.save(user);

        String roleName = user.getRole().getName().name();

        switch (roleName) {
            case "ROLE_ADMIN":
                    return "redirect:/admin/user_list";
            case "ROLE_PROCUREMENT_MANAGER":
                    return "redirect:/procurement-manager/home";
            case "ROLE_WAREHOUSE_MANAGER":
                return "redirect:/warehouse-manager/home";
            default:
                return "redirect:/main_page";
        }
    }
}
