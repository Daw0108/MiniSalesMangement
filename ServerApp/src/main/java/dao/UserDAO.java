package dao;

import model.User;
import utils.DatabaseConnection;
import java.sql.*;

public class UserDAO {

    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return null;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        User user = new User();
                        user.setId(rs.getInt("id"));
                        user.setUsername(rs.getString("username"));
                        user.setPasswordHash(rs.getString("password_hash"));
                        user.setRole(rs.getString("role"));
                        user.setStatus(rs.getString("status"));
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi findByUsername: " + e.getMessage());
        }
        return null;
    }

    public void incrementFailedAttempts(String username) {
        String sqlUpdateCount = "UPDATE users SET failed_attempts = failed_attempts + 1 WHERE username = ?";
        String sqlLock = "UPDATE users SET status = 'LOCKED' WHERE username = ? AND failed_attempts >= 3";
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateCount)) {
                stmt.setString(1, username);
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement(sqlLock)) {
                stmt.setString(1, username);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void resetFailedAttempts(String username) {
        String sql = "UPDATE users SET failed_attempts = 0 WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}