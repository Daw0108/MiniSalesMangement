package dao;

import model.Product;
import utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // 1. Hàm lấy toàn bộ danh sách sản phẩm (Giải quyết lỗi findAll)
    public List<Product> findAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (conn == null) return list;

            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setPrice(rs.getDouble("price"));
                p.setStock(rs.getInt("stock"));
                p.setStatus(rs.getString("status"));
                list.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách sản phẩm: " + e.getMessage());
        }
        return list;
    }

    // 2. Hàm thêm mới sản phẩm (Giải quyết lỗi insert)
    public boolean insert(Product p) {
        String sql = "INSERT INTO products (name, price, stock, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (conn == null) return false;

            stmt.setString(1, p.getName());
            stmt.setDouble(2, p.getPrice());
            stmt.setInt(3, p.getStock());
            stmt.setString(4, p.getStatus());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm sản phẩm: " + e.getMessage());
            return false;
        }
    }

    // 3. Hàm cập nhật thông tin sản phẩm
    public boolean update(Product p) {
        String sql = "UPDATE products SET name = ?, price = ?, stock = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (conn == null) return false;

            stmt.setString(1, p.getName());
            stmt.setDouble(2, p.getPrice());
            stmt.setInt(3, p.getStock());
            stmt.setString(4, p.getStatus());
            stmt.setInt(5, p.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật sản phẩm: " + e.getMessage());
            return false;
        }
    }

    // 4. Hàm xóa sản phẩm
    public boolean delete(int productId) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (conn == null) return false;

            stmt.setInt(1, productId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa sản phẩm: " + e.getMessage());
            return false;
        }
    }
}