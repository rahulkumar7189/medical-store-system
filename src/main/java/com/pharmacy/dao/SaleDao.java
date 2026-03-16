package com.pharmacy.dao;

import com.pharmacy.models.Sale;
import com.pharmacy.models.SaleItem;
import com.pharmacy.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class SaleDao {

    /**
     * Generates an invoice number in format: INV-YYYYMMDD-XXXX
     */
    public String generateInvoiceNumber() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String query = "SELECT COUNT(*) FROM Sales WHERE DATE(sale_date) = CURDATE()";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                int count = rs.getInt(1) + 1;
                return String.format("INV-%s-%04d", dateStr, count);
            }
        } catch (SQLException e) {
            System.err.println("Error generating invoice: " + e.getMessage());
        }
        return "INV-" + dateStr + "-0001";
    }

    /**
     * Executes a full Point-Of-Sale transaction with payment method and per-item tax.
     */
    public boolean processSale(Sale sale, List<SaleItem> items) {
        String insertSaleQuery = "INSERT INTO Sales (invoice_number, customer_id, user_id, total_amount, tax_amount, payment_method) VALUES (?, ?, ?, ?, ?, ?)";
        String insertSaleItemQuery = "INSERT INTO Sale_Items (sale_id, med_id, batch_number, quantity, price_at_sale, tax_rate) VALUES (?, ?, ?, ?, ?, ?)";
        String updateMedicineStockQuery = "UPDATE Medicines SET quantity = quantity - ? WHERE med_id = ?";

        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Insert Sale
            int generatedSaleId = -1;
            try (PreparedStatement saleStmt = conn.prepareStatement(insertSaleQuery, Statement.RETURN_GENERATED_KEYS)) {
                saleStmt.setString(1, sale.getInvoiceNumber());
                if (sale.getCustomerId() > 0) {
                    saleStmt.setInt(2, sale.getCustomerId());
                } else {
                    saleStmt.setNull(2, java.sql.Types.INTEGER);
                }
                saleStmt.setInt(3, sale.getUserId());
                saleStmt.setDouble(4, sale.getTotalAmount());
                saleStmt.setDouble(5, sale.getTaxAmount());
                saleStmt.setString(6, sale.getPaymentMethod());
                saleStmt.executeUpdate();

                try (ResultSet generatedKeys = saleStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedSaleId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Creating sale failed, no ID obtained.");
                    }
                }
            }

            // 2. Batch Insert Sale Items & 3. Deduct Stock
            try (PreparedStatement itemStmt = conn.prepareStatement(insertSaleItemQuery);
                 PreparedStatement stockStmt = conn.prepareStatement(updateMedicineStockQuery)) {

                for (SaleItem item : items) {
                    itemStmt.setInt(1, generatedSaleId);
                    itemStmt.setInt(2, item.getMedId());
                    itemStmt.setString(3, item.getBatchNumber());
                    itemStmt.setInt(4, item.getQuantity());
                    itemStmt.setDouble(5, item.getPriceAtSale());
                    itemStmt.setDouble(6, item.getTaxRate());
                    itemStmt.addBatch();

                    stockStmt.setInt(1, item.getQuantity());
                    stockStmt.setInt(2, item.getMedId());
                    stockStmt.addBatch();
                }

                itemStmt.executeBatch();
                stockStmt.executeBatch();
            }

            conn.commit();
            sale.setSaleId(generatedSaleId);
            return true;

        } catch (SQLException e) {
            System.err.println("Transaction failed. Rolling back. Error: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    /**
     * Get daily revenue for the current date
     */
    public double getDailyRevenue() {
        String query = "SELECT COALESCE(SUM(total_amount), 0) FROM Sales WHERE DATE(sale_date) = CURDATE()";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("Error fetching daily revenue: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Get total sales count for today
     */
    public int getDailySalesCount() {
        String query = "SELECT COUNT(*) FROM Sales WHERE DATE(sale_date) = CURDATE()";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error fetching daily sales count: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Get top 10 best-selling medicines
     */
    public Map<String, Integer> getBestSellers() {
        Map<String, Integer> bestSellers = new LinkedHashMap<>();
        String query = "SELECT m.name, SUM(si.quantity) as total_sold FROM Sale_Items si JOIN Medicines m ON si.med_id = m.med_id GROUP BY si.med_id, m.name ORDER BY total_sold DESC LIMIT 10";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                bestSellers.put(rs.getString("name"), rs.getInt("total_sold"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching best sellers: " + e.getMessage());
        }
        return bestSellers;
    }

    /**
     * Get revenue per day for the last N days
     */
    public Map<String, Double> getRevenueByDay(int lastNDays) {
        Map<String, Double> revenueMap = new LinkedHashMap<>();
        String query = "SELECT DATE(sale_date) as sale_day, SUM(total_amount) as revenue FROM Sales WHERE sale_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY) GROUP BY sale_day ORDER BY sale_day ASC";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, lastNDays);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    revenueMap.put(rs.getString("sale_day"), rs.getDouble("revenue"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching revenue by day: " + e.getMessage());
        }
        return revenueMap;
    }
}
