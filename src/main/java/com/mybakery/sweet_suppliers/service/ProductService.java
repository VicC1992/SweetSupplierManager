package com.mybakery.sweet_suppliers.service;

import com.mybakery.sweet_suppliers.entity.Product;
import com.mybakery.sweet_suppliers.entity.SupplierProduct;
import com.mybakery.sweet_suppliers.repository.ProductRepository;
import com.mybakery.sweet_suppliers.repository.SupplierProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SupplierProductRepository supplierProductRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }
    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Optional<Product> findByName(String name) {
        return productRepository.findByName(name);
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public List<SupplierProduct> getAllProductsOrderedByName() {
        return supplierProductRepository.findAll(Sort.by(Sort.Order.asc("product.name")));
    }
}
