package com.pharmacy.models;

import java.sql.Timestamp;

public class Sale {
    private int saleId;
    private String invoiceNumber;
    private int customerId;
    private String customerName;
    private String customerContact;
    private int userId;
    private double totalAmount;
    private double taxAmount;
    private String paymentMethod;
    private Timestamp saleDate;

    public Sale() {}

    public Sale(int saleId, String invoiceNumber, int customerId, String customerName, String customerContact, int userId, double totalAmount, double taxAmount, String paymentMethod, Timestamp saleDate) {
        this.saleId = saleId;
        this.invoiceNumber = invoiceNumber;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.taxAmount = taxAmount;
        this.paymentMethod = paymentMethod;
        this.saleDate = saleDate;
    }

    public int getSaleId() { return saleId; }
    public void setSaleId(int saleId) { this.saleId = saleId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerContact() { return customerContact; }
    public void setCustomerContact(String customerContact) { this.customerContact = customerContact; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getTaxAmount() { return taxAmount; }
    public void setTaxAmount(double taxAmount) { this.taxAmount = taxAmount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public Timestamp getSaleDate() { return saleDate; }
    public void setSaleDate(Timestamp saleDate) { this.saleDate = saleDate; }

    @Override
    public String toString() {
        return "Sale{" +
                "saleId=" + saleId +
                ", invoice='" + invoiceNumber + '\'' +
                ", userId=" + userId +
                ", totalAmount=" + totalAmount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", saleDate=" + saleDate +
                '}';
    }
}
