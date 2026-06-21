package model;

public class OrderItem {
    private int productId;
    private int quantity;
    private double price; // Giá bán tại thời điểm mua

    // Getters and Setters
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}