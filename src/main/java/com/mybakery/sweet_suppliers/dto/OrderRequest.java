package com.mybakery.sweet_suppliers.dto;

import com.mybakery.sweet_suppliers.Enums.OrderStatus;

public class OrderRequest {
    private String name;
    private OrderStatus status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
