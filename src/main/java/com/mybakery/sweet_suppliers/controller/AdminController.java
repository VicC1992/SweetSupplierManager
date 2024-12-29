package com.mybakery.sweet_suppliers.controller;

import com.mybakery.sweet_suppliers.entity.Supplier;
import com.mybakery.sweet_suppliers.entity.User;
import com.mybakery.sweet_suppliers.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.mybakery.sweet_suppliers.repository.RoleRepository;
import com.mybakery.sweet_suppliers.repository.UserRepository;
import com.mybakery.sweet_suppliers.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")

public class AdminController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @GetMapping("/suppliers")
    public String listSuppliers(Model model) {
        List<Supplier> listSuppliers = supplierRepository.findAll();
        model.addAttribute("listSuppliers", listSuppliers);
        return "suppliers_list";
    }

}
