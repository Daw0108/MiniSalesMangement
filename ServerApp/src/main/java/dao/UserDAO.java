package dao;

import model.User;
import utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    // Hàm tìm kiếm người dùng bằng Username (Khớp chính xác với hàm gọi ở ClientHandler)
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ? AND status = 'ACTIVE'";

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return null;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        User user = new User();
                        user.setId(rs.getInt("id"));
                        user.setUsername(rs.getString("username"));
                        // Lấy mật khẩu dạng chuỗi thô (ví dụ: "123456") từ database
                        user.setPasswordHash(rs.getString("password_hash"));
                        user.setRole(rs.getString("role"));
                        user.setStatus(rs.getString("status"));
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi truy vấn database trong UserDAO: " + e.getMessage());
        }
        return null;
    }
}