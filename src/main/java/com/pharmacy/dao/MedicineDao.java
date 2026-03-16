package com.pharmacy.dao;

import com.pharmacy.models.Medicine;
import com.pharmacy.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MedicineDao {

    public boolean addMedicine(Medicine med) {
        String query = "INSERT INTO Medicines (name, generic_name, batch_number, category, price, tax_rate, quantity, reorder_level, expiry_date, supplier_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, med.getName());
            pstmt.setString(2, med.getGenericName());
            pstmt.setString(3, med.getBatchNumber());
            pstmt.setString(4, med.getCategory());
            pstmt.setDouble(5, med.getPrice());
            pstmt.setDouble(6, med.getTaxRate());
            pstmt.setInt(7, med.getQuantity());
            pstmt.setInt(8, med.getReorderLevel());
            pstmt.setDate(9, med.getExpiryDate());

            if (med.getSupplierId() > 0) {
                pstmt.setInt(10, med.getSupplierId());
            } else {
                pstmt.setNull(10, java.sql.Types.INTEGER);
            }

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding medicine: " + e.getMessage());
            return false;
        }
    }

    public List<Medicine> getAllMedicines() {
        List<Medicine> medicines = new ArrayList<>();
        String query = "SELECT * FROM Medicines ORDER BY expiry_date ASC";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                medicines.add(mapResultSetToMedicine(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching medicines: " + e.getMessage());
        }
        return medicines;
    }

    public List<Medicine> getExpiringMedicines(int daysAhead) {
        List<Medicine> medicines = new ArrayList<>();
        String query = "SELECT * FROM Medicines WHERE expiry_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL ? DAY) ORDER BY expiry_date ASC";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, daysAhead);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    medicines.add(mapResultSetToMedicine(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching expiring medicines: " + e.getMessage());
        }
        return medicines;
    }

    public List<Medicine> getExpiredMedicines() {
        List<Medicine> medicines = new ArrayList<>();
        String query = "SELECT * FROM Medicines WHERE expiry_date < CURDATE()";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                medicines.add(mapResultSetToMedicine(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching expired medicines: " + e.getMessage());
        }
        return medicines;
    }

    public List<Medicine> getLowStockMedicines() {
        List<Medicine> medicines = new ArrayList<>();
        String query = "SELECT * FROM Medicines WHERE quantity <= reorder_level";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                medicines.add(mapResultSetToMedicine(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching low stock medicines: " + e.getMessage());
        }
        return medicines;
    }

    public boolean updateMedicineQuantity(int medId, int newQuantity) {
        String query = "UPDATE Medicines SET quantity = ? WHERE med_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, newQuantity);
            pstmt.setInt(2, medId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating quantity: " + e.getMessage());
            return false;
        }
    }

    public Medicine getMedicineByBatchNumber(String batchNumber) {
        String query = "SELECT * FROM Medicines WHERE batch_number = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, batchNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMedicine(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching by batch: " + e.getMessage());
        }
        return null;
    }

    private Medicine mapResultSetToMedicine(ResultSet rs) throws SQLException {
        return new Medicine(
                rs.getInt("med_id"),
                rs.getString("name"),
                rs.getString("generic_name"),
                rs.getString("batch_number"),
                rs.getString("category"),
                rs.getDouble("price"),
                rs.getDouble("tax_rate"),
                rs.getInt("quantity"),
                rs.getInt("reorder_level"),
                rs.getDate("expiry_date"),
                rs.getInt("supplier_id"),
                rs.getTimestamp("created_at")
        );
    }
    public List<Medicine> searchMedicines(String searchTerm) {
        List<Medicine> medicines = new ArrayList<>();
        String query = "SELECT * FROM Medicines WHERE name LIKE ? OR generic_name LIKE ? OR batch_number LIKE ? OR category LIKE ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            String like = "%" + searchTerm + "%";
            pstmt.setString(1, like);
            pstmt.setString(2, like);
            pstmt.setString(3, like);
            pstmt.setString(4, like);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    medicines.add(mapResultSetToMedicine(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching medicines: " + e.getMessage());
        }
        return medicines;
    }
}
