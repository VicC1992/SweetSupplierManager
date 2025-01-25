package com.mybakery.sweet_suppliers.entity;

import com.mybakery.sweet_suppliers.Enums.DeliveryDays;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "suppliers")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column(unique = true)
    private String registrationUniqueCode;

    @Column
    private String contactPerson;

    @Column(unique = true)
    private String phoneNumber;

    @ElementCollection(targetClass = DeliveryDays.class)
    @CollectionTable(
            name = "supplier_delivery_days",
            joinColumns = @JoinColumn(name = "supplier_id")
    )
    @Column(name = "delivery_day")
    @Enumerated(EnumType.STRING)
    private List<DeliveryDays> deliveryDays = new ArrayList<>();

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupplierProduct> supplierProducts = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegistrationUniqueCode() {
        return registrationUniqueCode;
    }

    public void setRegistrationUniqueCode(String registrationUniqueCode) {
        this.registrationUniqueCode = registrationUniqueCode;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (!phoneNumber.matches("\\+40\\d{9}")) {
            throw new IllegalArgumentException("The telephone number must be in the format: +40XXXXXXXXX");
        }
        this.phoneNumber = phoneNumber;
    }

    public List<DeliveryDays> getDeliveryDays() {
        return deliveryDays;
    }

    public void setDeliveryDays(List<DeliveryDays> deliveryDays) {
        this.deliveryDays = deliveryDays;
    }

    public List<SupplierProduct> getSupplierProducts() {
        return supplierProducts;
    }

    public void setSupplierProducts(List<SupplierProduct> supplierProducts) {
        this.supplierProducts = supplierProducts;
    }

    @Override
    public String toString() {
        return "Supplier{" +
                "name='" + name + '\'' +
                ", registrationUniqueCode='" + registrationUniqueCode + '\'' +
                ", contactPerson='" + contactPerson + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", deliveryDays=" + deliveryDays +
                '}';
    }
}
