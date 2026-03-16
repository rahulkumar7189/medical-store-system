package com.pharmacy.models;

import java.sql.Timestamp;

public class Customer {
    private int customerId;
    private String name;
    private String contactNumber;
    private Timestamp createdAt;

    public Customer() {}

    public Customer(int customerId, String name, String contactNumber, Timestamp createdAt) {
        this.customerId = customerId;
        this.name = name;
        this.contactNumber = contactNumber;
        this.createdAt = createdAt;
    }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
