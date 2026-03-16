package com.pharmacy.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseMigration {

    public static void runMigration() {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return;

        try (Statement stmt = conn.createStatement()) {
            // 1. Create Customers Table
            String createCustomersTable = "CREATE TABLE IF NOT EXISTS Customers (" +
                    "customer_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "contact_number VARCHAR(20) UNIQUE, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.execute(createCustomersTable);
            System.out.println("Migration: Customers table checked/created.");

            // 2. Ensuring V2 columns in Sales table
            ensureColumn(stmt, "Sales", "invoice_number", "VARCHAR(20) AFTER sale_id");
            ensureColumn(stmt, "Sales", "payment_method", "ENUM('CASH','CARD','UPI') DEFAULT 'CASH' AFTER tax_amount");
            ensureColumn(stmt, "Sales", "customer_id", "INT");

            // 3. Ensuring V2 columns in Sale_Items table
            ensureColumn(stmt, "Sale_Items", "batch_number", "VARCHAR(50) AFTER med_id");
            ensureColumn(stmt, "Sale_Items", "tax_rate", "DECIMAL(5,2) DEFAULT 5.00 AFTER price_at_sale");

            // 4. Checking and Adding Foreign Key for customer_id
            try {
                stmt.execute("ALTER TABLE Sales ADD FOREIGN KEY (customer_id) REFERENCES Customers(customer_id)");
                System.out.println("Migration: Linked Sales.customer_id to Customers table.");
            } catch (SQLException e) {
                // Usually fails if FK already exists, which is fine
            }

        } catch (SQLException e) {
            System.err.println("Migration failed: " + e.getMessage());
        }
    }

    private static void ensureColumn(Statement stmt, String table, String column, String definition) {
        try {
            stmt.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
            System.out.println("Migration: Added column " + column + " to " + table + ".");
        } catch (SQLException e) {
            if (e.getErrorCode() != 1060) { // 1060 is Duplicate Column Name
                System.err.println("Migration warning (" + table + "." + column + "): " + e.getMessage());
            }
        }
    }
}
