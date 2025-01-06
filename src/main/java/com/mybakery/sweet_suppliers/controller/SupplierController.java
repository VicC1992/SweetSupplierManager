package com.mybakery.sweet_suppliers.controller;

import com.mybakery.sweet_suppliers.entity.Supplier;
import com.mybakery.sweet_suppliers.repository.SupplierRepository;
import com.mybakery.sweet_suppliers.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("suppliers")
public class SupplierController {
    @Autowired
    private SupplierService supplierService;

    @Autowired
    SupplierRepository supplierRepository;

    @GetMapping("/list")
    public String listSuppliers(Model model) {
        List<Supplier> listSuppliers = supplierRepository.findAll();
        model.addAttribute("listSuppliers", listSuppliers);
        return "suppliers_list";
    }

    @GetMapping("/add")
    public String showSupplierForm(Model model) {
        model.addAttribute("supplier", new Supplier());
        return "add_new_supplier_form";
    }

    @PostMapping("/add")
    public String addSupplier(Supplier supplier, String deliveryDays) {
        supplier.setDeliveryDays(Arrays.asList(deliveryDays.split(",")));
        supplierService.addSupplier(supplier);
        return "redirect:/suppliers";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Supplier supplier = supplierService.findById(id);
        model.addAttribute("supplier", supplier);
        return "edit_supplier_form";
    }

    @PostMapping("/edit/{id}")
    public String updateSupplier(@PathVariable Long id, Supplier supplier, String deliveryDays) {
        Supplier existingSupplier = supplierService.findById(id);
        existingSupplier.setName(supplier.getName());
        existingSupplier.setRegistrationUniqueCode(supplier.getRegistrationUniqueCode());
        existingSupplier.setContactPerson(supplier.getContactPerson());
        existingSupplier.setPhoneNumber(supplier.getPhoneNumber());
        existingSupplier.setDeliveryDays(new ArrayList<>(Arrays.asList(deliveryDays.split(","))));
        supplierService.updateSupplier(existingSupplier);
        return "redirect:/suppliers";
    }

    @PostMapping("/delete/{id}")
    public String deleteSupplier(@PathVariable Long id) {
        supplierService.deleteById(id);
        return "redirect:/suppliers";
    }
}
