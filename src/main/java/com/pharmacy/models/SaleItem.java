package com.pharmacy.models;

public class SaleItem {
    private int saleItemId;
    private int saleId;
    private int medId;
    private String batchNumber;
    private String medicineName;
    private int quantity;
    private double priceAtSale;
    private double taxRate;

    public SaleItem() {}

    public SaleItem(int saleItemId, int saleId, int medId, String batchNumber, int quantity, double priceAtSale, double taxRate) {
        this.saleItemId = saleItemId;
        this.saleId = saleId;
        this.medId = medId;
        this.batchNumber = batchNumber;
        this.quantity = quantity;
        this.priceAtSale = priceAtSale;
        this.taxRate = taxRate;
    }

    public int getSaleItemId() { return saleItemId; }
    public void setSaleItemId(int saleItemId) { this.saleItemId = saleItemId; }

    public int getSaleId() { return saleId; }
    public void setSaleId(int saleId) { this.saleId = saleId; }

    public int getMedId() { return medId; }
    public void setMedId(int medId) { this.medId = medId; }

    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPriceAtSale() { return priceAtSale; }
    public void setPriceAtSale(double priceAtSale) { this.priceAtSale = priceAtSale; }

    public double getTaxRate() { return taxRate; }
    public void setTaxRate(double taxRate) { this.taxRate = taxRate; }

    // Computed fields for cart display
    public double getLineTotal() { return priceAtSale * quantity; }
    public double getLineTax() { return getLineTotal() * (taxRate / 100.0); }
    public double getLineGrandTotal() { return getLineTotal() + getLineTax(); }

    @Override
    public String toString() {
        return "SaleItem{" +
                "medId=" + medId +
                ", batch='" + batchNumber + '\'' +
                ", quantity=" + quantity +
                ", priceAtSale=" + priceAtSale +
                ", taxRate=" + taxRate +
                '}';
    }
}
