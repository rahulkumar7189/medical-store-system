package com.pharmacy.dao;

import com.pharmacy.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.pharmacy.models.AuditLog;

public class AuditLogDao {

    public void logAction(int userId, String action, String details) {
        String query = "INSERT INTO Audit_Log (user_id, action, details) VALUES (?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, action);
            pstmt.setString(3, details);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error logging audit action: " + e.getMessage());
        }
    }

    public List<AuditLog> getAllLogs() {
        List<AuditLog> logs = new ArrayList<>();
        String query = "SELECT a.*, u.username FROM Audit_Log a LEFT JOIN Users u ON a.user_id = u.user_id ORDER BY a.timestamp DESC LIMIT 500";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                AuditLog log = new AuditLog();
                log.setLogId(rs.getInt("log_id"));
                log.setUserId(rs.getInt("user_id"));
                log.setUsername(rs.getString("username"));
                log.setAction(rs.getString("action"));
                log.setDetails(rs.getString("details"));
                log.setTimestamp(rs.getTimestamp("timestamp"));
                logs.add(log);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching audit logs: " + e.getMessage());
        }
        return logs;
    }

    public List<AuditLog> searchLogs(String searchTerm) {
        List<AuditLog> logs = new ArrayList<>();
        String query = "SELECT a.*, u.username FROM Audit_Log a LEFT JOIN Users u ON a.user_id = u.user_id WHERE a.action LIKE ? OR a.details LIKE ? OR u.username LIKE ? ORDER BY a.timestamp DESC LIMIT 200";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            String like = "%" + searchTerm + "%";
            pstmt.setString(1, like);
            pstmt.setString(2, like);
            pstmt.setString(3, like);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    AuditLog log = new AuditLog();
                    log.setLogId(rs.getInt("log_id"));
                    log.setUserId(rs.getInt("user_id"));
                    log.setUsername(rs.getString("username"));
                    log.setAction(rs.getString("action"));
                    log.setDetails(rs.getString("details"));
                    log.setTimestamp(rs.getTimestamp("timestamp"));
                    logs.add(log);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching audit logs: " + e.getMessage());
        }
        return logs;
    }
}
