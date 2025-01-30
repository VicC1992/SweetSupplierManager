package com.mybakery.sweet_suppliers.controller;

import com.mybakery.sweet_suppliers.Enums.DeliveryDays;
import com.mybakery.sweet_suppliers.entity.Supplier;
import com.mybakery.sweet_suppliers.repository.SupplierRepository;
import com.mybakery.sweet_suppliers.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String addSupplier(Supplier supplier, @RequestParam List<DeliveryDays> deliveryDays) {
        supplier.setDeliveryDays(deliveryDays);
        supplierService.addSupplier(supplier);
        return "redirect:/suppliers/list";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Supplier supplier = supplierService.findById(id);
        model.addAttribute("supplier", supplier);
        return "edit_supplier_form";
    }

    @PostMapping("/edit/{id}")
    public String updateSupplier(@PathVariable Long id,@ModelAttribute Supplier supplier) {
        Supplier existingSupplier = supplierService.findById(id);
        existingSupplier.setName(supplier.getName());
        existingSupplier.setRegistrationUniqueCode(supplier.getRegistrationUniqueCode());
        existingSupplier.setContactPerson(supplier.getContactPerson());
        existingSupplier.setPhoneNumber(supplier.getPhoneNumber());
        existingSupplier.setDeliveryDays(supplier.getDeliveryDays());
        supplierService.updateSupplier(existingSupplier);
        return "redirect:/suppliers/list";
    }

    @PostMapping("/delete/{id}")
    public String deleteSupplier(@PathVariable Long id) {
        supplierService.deleteById(id);
        return "redirect:/suppliers/list";
    }

    ///////

    @GetMapping("/add-edit-form")
    public String showSupplierForm(@RequestParam(required = false) Long id, Model model) {
        Supplier supplier = (id != null) ? supplierService.findById(id) : new Supplier();
        model.addAttribute("supplier", supplier);
        return "add_edit_supplier";
    }

    @PostMapping("/save-edit")
    public String addEditSupplier(@ModelAttribute Supplier supplier, @RequestParam List<DeliveryDays> deliveryDays) {
        if (supplier.getId() != null) {
            Supplier existingSupplier = supplierService.findById(supplier.getId());
            existingSupplier.setName(supplier.getName());
            existingSupplier.setRegistrationUniqueCode(supplier.getRegistrationUniqueCode());
            existingSupplier.setContactPerson(supplier.getContactPerson());
            existingSupplier.setPhoneNumber(supplier.getPhoneNumber());
            existingSupplier.setDeliveryDays(supplier.getDeliveryDays());
            supplierService.updateSupplier(existingSupplier);
        } else {
            supplier.setDeliveryDays(deliveryDays);
            supplierService.addSupplier(supplier);
        }
        return "redirect:/suppliers/list";
    }
}
