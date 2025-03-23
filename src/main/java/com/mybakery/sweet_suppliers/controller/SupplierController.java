package com.mybakery.sweet_suppliers.controller;

import com.mybakery.sweet_suppliers.Enums.DeliveryDays;
import com.mybakery.sweet_suppliers.Enums.SupplierStatus;
import com.mybakery.sweet_suppliers.entity.Supplier;
import com.mybakery.sweet_suppliers.repository.SupplierRepository;
import com.mybakery.sweet_suppliers.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        List<Supplier> listSuppliers = supplierService.getActiveSuppliers();
        model.addAttribute("listSuppliers", listSuppliers);
        return "suppliers_list";
    }

    @PostMapping("/set-inactiv/{id}")
    public String setInactivStatus(@PathVariable Long id) {
        Supplier supplier = supplierService.findById(id);
        supplier.setSupplierStatus(SupplierStatus.Inactiv);
        supplierService.saveSupplier(supplier);
        return "redirect:/suppliers/list";
    }

    @GetMapping("/create-edit-form")
    public String showSupplierForm(@RequestParam(required = false) Long id, Model model) {
        Supplier supplier = (id != null) ? supplierService.findById(id) : new Supplier();
        model.addAttribute("supplier", supplier);
        return "add_edit_supplier_form";
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
            existingSupplier.setSupplierStatus(supplier.getSupplierStatus());
            supplierService.updateSupplier(existingSupplier);
        } else {
            supplier.setDeliveryDays(deliveryDays);
            supplier.setSupplierStatus(SupplierStatus.Activ);
            supplierService.addSupplier(supplier);
        }
        return "redirect:/suppliers/list";
    }
}
