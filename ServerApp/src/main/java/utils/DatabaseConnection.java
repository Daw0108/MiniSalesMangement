package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/sales_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&useUnicode=yes&characterEncoding=UTF-8";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Mặc định XAMPP để trống

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Luôn tạo kết nối mới khi được gọi để tránh lỗi ConnectionIsClosedException giữa các luồng
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println(">> Lỗi: Thiếu Driver MySQL trong pom.xml!");
            return null;
        } catch (SQLException e) {
            System.err.println(">> Lỗi: Không thể kết nối MySQL! Hãy chắc chắn đã bấm START MySQL trong XAMPP.");
            return null;
        }
    }
}