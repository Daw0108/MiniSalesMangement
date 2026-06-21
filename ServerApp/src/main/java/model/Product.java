package model;

import java.io.Serializable;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private double price;
    private int stock;
    private String status;

    // 1. Hàm tạo không tham số bắt buộc cho Gson
    public Product() {
    }

    // 2. Hàm tạo 4 tham số giúp giải quyết lỗi ở ProductPanel dòng 199
    public Product(String name, double price, int stock, String status) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.status = status;
    }

    // 3. Hàm tạo đầy đủ tham số
    public Product(int id, String name, double price, int stock, String status) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.status = status;
    }

    // --- Toàn bộ Getter và Setter chuẩn ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}