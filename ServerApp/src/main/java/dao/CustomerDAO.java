package dao;

import model.Customer;
import utils.DatabaseConnection;
import utils.SecurityUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CustomerDAO {

    public boolean insert(Customer customer) {
        String sql = "INSERT INTO customers (name, phone_encrypted, address) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getName());

            String encryptedPhone = SecurityUtil.encryptAES(customer.getPhone());
            stmt.setString(2, encryptedPhone);

            stmt.setString(3, customer.getAddress());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

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