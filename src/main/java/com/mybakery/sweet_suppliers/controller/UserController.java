package com.mybakery.sweet_suppliers.controller;

import com.mybakery.sweet_suppliers.entity.Role;
import com.mybakery.sweet_suppliers.entity.Supplier;
import com.mybakery.sweet_suppliers.entity.User;
import com.mybakery.sweet_suppliers.repository.SupplierRepository;
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
        Role userRole = roleRepository.findByName("USER").orElseThrow(()-> new RuntimeException("Role USER not found"));
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

    @GetMapping("/suppliers")
    public String listSuppliers(Model model) {
        List<Supplier> listSuppliers = supplierRepository.findAll();
        model.addAttribute("listSuppliers", listSuppliers);
        return "suppliers_list";
    }
}
