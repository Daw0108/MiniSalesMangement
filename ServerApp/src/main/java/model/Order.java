package model;

import java.util.List;

public class Order {
    private int userId;
    private int customerId;
    private double totalAmount;
    private List<OrderItem> items;

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}