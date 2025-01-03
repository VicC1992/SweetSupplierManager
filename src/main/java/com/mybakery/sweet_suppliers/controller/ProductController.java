package com.mybakery.sweet_suppliers.controller;

import com.mybakery.sweet_suppliers.Enums.UnitOfMeasure;
import com.mybakery.sweet_suppliers.entity.Product;
import com.mybakery.sweet_suppliers.entity.Supplier;
import com.mybakery.sweet_suppliers.entity.SupplierProduct;
import com.mybakery.sweet_suppliers.repository.SupplierProductRepository;
import com.mybakery.sweet_suppliers.service.ProductService;
import com.mybakery.sweet_suppliers.service.SupplierProductService;
import com.mybakery.sweet_suppliers.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ProductController {
    @Autowired
    private SupplierProductRepository supplierProductRepository;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SupplierProductService supplierProductService;

    @GetMapping("/suppliers/{supplierId}/products")
    public String viewSupplierProducts(@PathVariable Long supplierId, Model model) {
        Supplier supplier = supplierService.findById(supplierId);
        List<SupplierProduct> supplierProducts = supplier.getSupplierProducts();
        model.addAttribute("supplier", supplier);
        model.addAttribute("products", supplierProducts);
        return "products_list";
    }

    @GetMapping("/suppliers/{supplierId}/add-product")
    public String showAddProductForm(@PathVariable Long supplierId, Model model) {
        Supplier supplier = supplierService.findById(supplierId);
        model.addAttribute("supplierId", supplierId);
        model.addAttribute("supplierProduct", new SupplierProduct());
        model.addAttribute("products", productService.findAll());
        model.addAttribute("unitsOfMeasure", UnitOfMeasure.values());
        return "add_new_product_form";
    }

    @PostMapping("/suppliers/{supplierId}/add-product")
    public String addProductToSupplier(
            @PathVariable Long supplierId,
            @ModelAttribute SupplierProduct supplierProduct) {

        Supplier supplier = supplierService.findById(supplierId);

        String productName = supplierProduct.getProduct().getName();
        Product product = productService.findByName(productName)
                .orElseGet(() -> {
                    Product newProduct = new Product();
                    newProduct.setName(productName);
                    return productService.save(newProduct);
                });

        supplierProduct.setSupplier(supplier);
        supplierProduct.setProduct(product);

        supplier.getSupplierProducts().add(supplierProduct);
        supplierService.saveSupplier(supplier);
        return "redirect:/suppliers/" + supplierId + "/products";
    }

    @GetMapping("/suppliers/{supplierId}/edit-product/{supplierProductId}")
    public String showEditProductForm(@PathVariable Long supplierId, @PathVariable Long supplierProductId, Model model) {
        SupplierProduct supplierProduct = supplierProductService.findById(supplierProductId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid supplier product ID:" + supplierProductId));
        Supplier supplier = supplierService.findById(supplierId);
            model.addAttribute("supplierProduct", supplierProduct);
            model.addAttribute("supplierId", supplierId);
            model.addAttribute("supplier", supplier);
            model.addAttribute("supplierProductId", supplierProductId);
            model.addAttribute("unitsOfMeasure", UnitOfMeasure.values());
            return "edit_product_form";
    }

    @PostMapping("/suppliers/{supplierId}/edit-product/{supplierProductId}")
    public String editProduct(@PathVariable Long supplierId, @PathVariable Long supplierProductId, @ModelAttribute SupplierProduct supplierProduct) {
        SupplierProduct existingProduct = supplierProductService.findById(supplierProductId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid supplier product ID:" + supplierProductId));
            existingProduct.setPrice(supplierProduct.getPrice());
            existingProduct.setUnitOfMeasure(supplierProduct.getUnitOfMeasure());
            existingProduct.setProductDescription(supplierProduct.getProductDescription());
            existingProduct.setNonConformities(supplierProduct.getNonConformities());
            existingProduct.setProductStatus(supplierProduct.getProductStatus());

            supplierProductService.save(existingProduct);
            return "redirect:/suppliers/" + supplierId + "/products";
    }

    @PostMapping("/suppliers/{supplierId}/delete-product/{supplierProductId}")
    public String deleteProduct(@PathVariable Long supplierId, @PathVariable Long supplierProductId) {
        SupplierProduct existingProduct = supplierProductService.findById(supplierProductId)
                        .orElseThrow(()->new IllegalArgumentException("Invalid product ID:" + supplierProductId));
        supplierProductService.deleteById(supplierProductId);
        return "redirect:/suppliers/" + supplierId + "/products";
    }
}
