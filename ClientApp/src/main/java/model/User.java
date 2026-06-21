package model;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String username;
    private String passwordHash;
    private String role;
    private String status; // Thêm thuộc tính status để khớp với DB

    public User() {
    }

    public User(int id, String username, String passwordHash, String role, String status) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    // Giải quyết triệt để lỗi gạch đỏ setStatus ở UserDAO
    public void setStatus(String status) {
        this.status = status;
    }
}