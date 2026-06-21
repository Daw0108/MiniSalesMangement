package dao;

import model.Order;
import model.OrderItem;
import utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class OrderDAO {

    public boolean createOrderWithTransaction(Order order) {
        Connection conn = DatabaseConnection.getConnection();

        String insertOrderSql = "INSERT INTO orders (user_id, customer_id, total_amount) VALUES (?, ?, ?)";
        String insertItemSql = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
        String updateStockSql = "UPDATE products SET stock = stock - ? WHERE id = ? AND stock >= ?";

        try {
            // 1. Tắt Auto-Commit
            conn.setAutoCommit(false);

            // 2. Insert Order
            int newOrderId = -1;
            try (PreparedStatement stmtOrder = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                stmtOrder.setInt(1, order.getUserId());
                stmtOrder.setInt(2, order.getCustomerId());
                stmtOrder.setDouble(3, order.getTotalAmount());
                stmtOrder.executeUpdate();

                ResultSet rs = stmtOrder.getGeneratedKeys();
                if (rs.next()) {
                    newOrderId = rs.getInt(1);
                } else {
                    throw new Exception("Lỗi tạo ID Hoá đơn");
                }
            }

            // 3. Insert Order Items & Update Stock
            try (PreparedStatement stmtItem = conn.prepareStatement(insertItemSql);
                 PreparedStatement stmtStock = conn.prepareStatement(updateStockSql)) {

                for (OrderItem item : order.getItems()) {
                    stmtItem.setInt(1, newOrderId);
                    stmtItem.setInt(2, item.getProductId());
                    stmtItem.setInt(3, item.getQuantity());
                    stmtItem.setDouble(4, item.getPrice());
                    stmtItem.executeUpdate();

                    stmtStock.setInt(1, item.getQuantity());
                    stmtStock.setInt(2, item.getProductId());
                    stmtStock.setInt(3, item.getQuantity());

                    if (stmtStock.executeUpdate() == 0) {
                        throw new Exception("Sản phẩm ID " + item.getProductId() + " không đủ hàng!");
                    }
                }
            }

            // 4. Thành công -> Commit
            conn.commit();
            return true;

        } catch (Exception e) {
            System.err.println("Rollback Transaction: " + e.getMessage());
            try { conn.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (Exception ex) { ex.printStackTrace(); }
        }
    }
}