package com.pharmacy.dao;

import com.pharmacy.models.Customer;
import com.pharmacy.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDao {

    public int getOrCreateCustomer(String name, String contact) {
        if (contact == null || contact.trim().isEmpty()) return -1;

        String checkQuery = "SELECT customer_id FROM Customers WHERE contact_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkQuery)) {
            pstmt.setString(1, contact);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("customer_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Create new customer
        String insertQuery = "INSERT INTO Customers (name, contact_number) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setString(2, contact);
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM Customers ORDER BY name ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                customers.add(new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("contact_number"),
                        rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public List<String> getCustomerPurchaseHistory(int customerId) {
        List<String> history = new ArrayList<>();
        // Note: Sale_Items uses price_at_sale. We calculate total including tax for the history display.
        String query = "SELECT s.invoice_number, m.name, si.quantity, si.price_at_sale, si.tax_rate, s.sale_date " +
                       "FROM Sales s " +
                       "JOIN Sale_Items si ON s.sale_id = si.sale_id " +
                       "JOIN Medicines m ON si.med_id = m.med_id " +
                       "WHERE s.customer_id = ? " +
                       "ORDER BY s.sale_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    double price = rs.getDouble("price_at_sale");
                    int qty = rs.getInt("quantity");
                    double taxRate = rs.getDouble("tax_rate");
                    double lineTotal = (price * qty) * (1 + taxRate / 100);

                    String record = String.format("[%s] %s x%d - \u20b9%.2f (%s)",
                            rs.getString("invoice_number"),
                            rs.getString("name"),
                            qty,
                            lineTotal,
                            rs.getTimestamp("sale_date").toString());
                    history.add(record);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching customer history: " + e.getMessage());
        }
        return history;
    }
}
