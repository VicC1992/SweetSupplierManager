package com.mybakery.sweet_suppliers.service;

import com.mybakery.sweet_suppliers.entity.Product;
import com.mybakery.sweet_suppliers.entity.RestockItem;
import com.mybakery.sweet_suppliers.repository.RestockItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RestockItemService {
    @Autowired
    private RestockItemRepository restockItemRepository;

    public List<RestockItem>getAllItems() {
        return restockItemRepository.findAll();
    }

    public void addItem(Product product, String quantity) {
        RestockItem item = new RestockItem();
        item.setProduct(product);
        item.setExistingQuantity(quantity);
        item.setAddedAt(LocalDateTime.now());
        restockItemRepository.save(item);
    }

    public void removeItem(Long id) {
        restockItemRepository.deleteById(id);
    }
}
