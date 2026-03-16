package com.pharmacy.models;

import java.sql.Date;
import java.sql.Timestamp;

public class Medicine {
    private int medId;
    private String name;
    private String genericName;
    private String batchNumber;
    private String category;
    private double price;
    private double taxRate;
    private int quantity;
    private int reorderLevel;
    private Date expiryDate;
    private int supplierId;
    private Timestamp createdAt;

    public Medicine() {}

    public Medicine(int medId, String name, String genericName, String batchNumber, String category,
                    double price, double taxRate, int quantity, int reorderLevel,
                    Date expiryDate, int supplierId, Timestamp createdAt) {
        this.medId = medId;
        this.name = name;
        this.genericName = genericName;
        this.batchNumber = batchNumber;
        this.category = category;
        this.price = price;
        this.taxRate = taxRate;
        this.quantity = quantity;
        this.reorderLevel = reorderLevel;
        this.expiryDate = expiryDate;
        this.supplierId = supplierId;
        this.createdAt = createdAt;
    }

    public int getMedId() { return medId; }
    public void setMedId(int medId) { this.medId = medId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGenericName() { return genericName; }
    public void setGenericName(String genericName) { this.genericName = genericName; }

    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getTaxRate() { return taxRate; }
    public void setTaxRate(double taxRate) { this.taxRate = taxRate; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(int reorderLevel) { this.reorderLevel = reorderLevel; }

    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }

    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Medicine{" +
                "medId=" + medId +
                ", name='" + name + '\'' +
                ", batch='" + batchNumber + '\'' +
                ", price=" + price +
                ", taxRate=" + taxRate +
                ", quantity=" + quantity +
                ", reorderLevel=" + reorderLevel +
                ", expiryDate=" + expiryDate +
                '}';
    }
}
