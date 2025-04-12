package com.mybakery.sweet_suppliers.controller;

import com.mybakery.sweet_suppliers.Enums.ProductStatus;
import com.mybakery.sweet_suppliers.Enums.UnitOfMeasure;
import com.mybakery.sweet_suppliers.entity.Product;
import com.mybakery.sweet_suppliers.entity.Supplier;
import com.mybakery.sweet_suppliers.entity.SupplierProduct;
import com.mybakery.sweet_suppliers.repository.ProductRepository;
import com.mybakery.sweet_suppliers.repository.SupplierProductRepository;
import com.mybakery.sweet_suppliers.service.ProductService;
import com.mybakery.sweet_suppliers.service.RestockItemService;
import com.mybakery.sweet_suppliers.service.SupplierProductService;
import com.mybakery.sweet_suppliers.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("products")
public class ProductController {
    @Autowired
    private SupplierProductRepository supplierProductRepository;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SupplierProductService supplierProductService;

    @Autowired
    private RestockItemService restockItemService;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/list/{supplierId}")
    public String viewSupplierProducts(@PathVariable Long supplierId, Model model) {
        Supplier supplier = supplierService.findById(supplierId);
        List<SupplierProduct> supplierProducts = supplierProductService.getSupplierProductsSorted(supplierId);
        model.addAttribute("supplier", supplier);
        model.addAttribute("products", supplierProducts);
        return "products_list";
    }

    @GetMapping({"/create-edit-form/{supplierId}", "/create-edit-form/{supplierId}/{supplierProductId}"})
    public String showProductForm(@PathVariable(required = false) Long supplierProductId,@PathVariable Long supplierId, Model model) {
        Supplier supplier = supplierService.findById(supplierId);
        SupplierProduct supplierProduct = (supplierProductId != null) ? supplierProductService.findById(supplierProductId)
                .orElseThrow(()-> new IllegalArgumentException("Invalid product ID:" + supplierProductId))
                :new SupplierProduct();
        model.addAttribute("supplierProduct", supplierProduct);
        model.addAttribute("supplierId", supplierId);
        model.addAttribute("unitsOfMeasure", UnitOfMeasure.values());
        model.addAttribute("productStatuses", ProductStatus.values());
        return "add_edit_product_form";
    }

    @PostMapping("/save-edit/{supplierId}")
    public String saveProduct(@PathVariable Long supplierId, @ModelAttribute SupplierProduct supplierProduct) {
        Supplier supplier = supplierService.findById(supplierId);
        if (supplierProduct.getId() != null) {
            SupplierProduct existingProduct = supplierProductService.findById(supplierProduct.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid supplier product ID:" + supplierProduct.getId()));
            existingProduct.setPrice(supplierProduct.getPrice());
            existingProduct.setUnitOfMeasure(supplierProduct.getUnitOfMeasure());
            existingProduct.setProductDescription(supplierProduct.getProductDescription());
            existingProduct.setNonConformities(supplierProduct.getNonConformities());
            existingProduct.setProductStatus(supplierProduct.getProductStatus());

            supplierProductService.save(existingProduct);
        } else {
            String productName = supplierProduct.getProduct().getName();
            Product product = productService.findByName(productName)
                    .orElseGet(()-> {
                        Product newProduct = new Product();
                        newProduct.setName(productName);
                        return productService.save(newProduct);
                    });
            supplierProduct.setSupplier(supplier);
            supplierProduct.setProduct(product);

            supplier.getSupplierProducts().add(supplierProduct);
            supplierService.saveSupplier(supplier);
        }
        return "redirect:/products/list/" + supplierId;
    }

    @PostMapping("/delete/{supplierId}/{supplierProductId}")
    public String deleteProduct(@PathVariable Long supplierId, @PathVariable Long supplierProductId) {
        SupplierProduct existingProduct = supplierProductService.findById(supplierProductId)
                        .orElseThrow(()->new IllegalArgumentException("Invalid product ID:" + supplierProductId));
        supplierProductService.deleteById(supplierProductId);
        return "redirect:/products/list/" + supplierId;
    }

    @GetMapping("/see-all")
    public String getAllProducts(@RequestParam(value = "productName", required = false) String productName, Model model) {
        List<SupplierProduct> supplierProducts;
        if (productName == null || productName.isEmpty()) {
            supplierProducts = productService.getAllProductsOrderedByName();
        } else {
            supplierProducts = productService.getProductsByName(productName);
        }
        List<Product> availableProducts = productService.getAllUniqueProducts();
        model.addAttribute("supplierProducts", supplierProducts);
        model.addAttribute("productStatuses", ProductStatus.values());
        model.addAttribute("availableProducts", availableProducts);
        model.addAttribute("selectedProductName", productName);
        return "all_products_list";
    }

    @PostMapping("/change-status/{supplierProductId}")
    public String changeProductStatus(@PathVariable Long supplierProductId,
                                      @RequestParam("productStatus") ProductStatus productStatus) {
        SupplierProduct supplierProduct = supplierProductService.findById(supplierProductId)
                .orElseThrow(()-> new IllegalArgumentException("Invalid supplier product:"));
        if (supplierProduct != null) {
            supplierProduct.setProductStatus(productStatus);
            supplierProductService.save(supplierProduct);
        }
        return "redirect:/products/see-all";
    }

    @GetMapping("/restock-list")
    public String showRestockList(Model model) {
        model.addAttribute("restockItems", restockItemService.getAllItems());
        model.addAttribute("products",productRepository.findAll());
        return "restock_list";
    }

    @PostMapping("/restock/add")
    public String addRestockItem(@RequestParam Long productId, @RequestParam String quantity) {
        Product product = productRepository.findById(productId).orElseThrow();
        restockItemService.addItem(product, quantity);
        return "redirect:/products/restock-list";
    }

    @PostMapping("/restock-item/remove/{id}")
    public String removeRestockItem(@PathVariable Long id) {
        restockItemService.removeItem(id);
        return "redirect:/products/restock-list";
    }

}
