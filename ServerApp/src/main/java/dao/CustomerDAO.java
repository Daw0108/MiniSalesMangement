package dao;

import model.Customer;
import utils.DatabaseConnection;
import utils.SecurityUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CustomerDAO {

    // Thêm khách hàng mới
    public boolean insert(Customer customer) {
        String sql = "INSERT INTO customers (name, phone_encrypted, address) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getName());

            // MÃ HOÁ số điện thoại trước khi lưu xuống database
            String encryptedPhone = SecurityUtil.encryptAES(customer.getPhone());
            stmt.setString(2, encryptedPhone);

            stmt.setString(3, customer.getAddress());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Tìm khách hàng theo ID
    public Customer findById(int id) {
        String sql = "SELECT * FROM customers WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Customer c = new Customer();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));

                // GIẢI MÃ số điện thoại khi lấy từ database lên
                String decryptedPhone = SecurityUtil.decryptAES(rs.getString("phone_encrypted"));
                c.setPhone(decryptedPhone);

                c.setAddress(rs.getString("address"));
                return c;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}