package ui;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Product;
import network.Request;
import network.Response;
import network.ServerConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ProductPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtName, txtPrice, txtStock;
    private JComboBox<String> cbStatus;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;
    private final Gson gson = new Gson();

    public ProductPanel() {
        setLayout(new BorderLayout(10, 10));
        initUI();
        loadProductData(); // Tự động lấy dữ liệu khi mở tab
    }

    private void initUI() {
        // 1. Khởi tạo Bảng dữ liệu sản phẩm
        tableModel = new DefaultTableModel(new String[]{"ID", "Tên sản phẩm", "Giá bán", "Số lượng kho", "Trạng thái"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Sự kiện click chuột vào bảng -> Đổ ngược dữ liệu lên Form nhập liệu
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    txtId.setText(tableModel.getValueAt(selectedRow, 0).toString());
                    txtName.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    txtPrice.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    txtStock.setText(tableModel.getValueAt(selectedRow, 3).toString());
                    cbStatus.setSelectedItem(tableModel.getValueAt(selectedRow, 4).toString());
                }
            }
        });

        // 2. Khu vực Form nhập liệu (Phía Đông hoặc Phía Nam)
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin sản phẩm"));

        formPanel.add(new JLabel("Mã SP (Không sửa):"));
        txtId = new JTextField();
        txtId.setEditable(false); // Chặn sửa ID bằng tay
        formPanel.add(txtId);

        formPanel.add(new JLabel("Tên sản phẩm:"));
        txtName = new JTextField();
        formPanel.add(txtName);

        formPanel.add(new JLabel("Giá bán:"));
        txtPrice = new JTextField();
        formPanel.add(txtPrice);

        formPanel.add(new JLabel("Số lượng kho:"));
        txtStock = new JTextField();
        formPanel.add(txtStock);

        formPanel.add(new JLabel("Trạng thái:"));
        cbStatus = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE"});
        formPanel.add(cbStatus);

        // 3. Hàng nút bấm chức năng
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnAdd = new JButton("Thêm mới");
        btnUpdate = new JButton("Sửa (Cập nhật)");
        btnDelete = new JButton("Xóa sản phẩm");
        btnRefresh = new JButton("Làm mới bảng");

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);

        // Đóng gói bố cục panel nhập liệu và nút bấm đặt ở phía dưới (SOUTH)
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(btnPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);

        // Gắn sự kiện lắng nghe nút bấm
        btnAdd.addActionListener(e -> handleAddProduct());
        btnUpdate.addActionListener(e -> handleUpdateProduct());
        btnDelete.addActionListener(e -> handleDeleteProduct());
        btnRefresh.addActionListener(e -> loadProductData());
    }

    // --- Hàm tải danh sách sản phẩm lên JTable ---
    private void loadProductData() {
        new SwingWorker<List<Product>, Void>() {
            @Override
            protected List<Product> doInBackground() throws Exception {
                Request req = new Request("GET_PRODUCTS", "");
                Response res = new ServerConnection().sendRequest(req);
                if (res != null && "SUCCESS".equals(res.getStatus())) {
                    return gson.fromJson(res.getData(), new TypeToken<List<Product>>(){}.getType());
                }
                return null;
            }
            @Override
            protected void done() {
                try {
                    List<Product> list = get();
                    tableModel.setRowCount(0); // Xóa dữ liệu cũ trên bảng hiển thị
                    if (list != null) {
                        for (Product p : list) {
                            tableModel.addRow(new Object[]{p.getId(), p.getName(), p.getPrice(), p.getStock(), p.getStatus()});
                        }
                    }
                } catch (Exception ignored) {}
            }
        }.execute();
    }

    // --- Hàm xử lý THÊM MỚI sản phẩm ---
    private void handleAddProduct() {
        Product p = getProductFromForm();
        if (p == null) return;

        try {
            Request req = new Request("ADD_PRODUCT", gson.toJson(p));
            Response res = new ServerConnection().sendRequest(req);

            if (res != null && "SUCCESS".equals(res.getStatus())) {
                JOptionPane.showMessageDialog(this, "Thêm thành công!");
                loadProductData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi thêm sản phẩm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (java.io.IOException | ClassNotFoundException ex) { // Bổ sung thêm ClassNotFoundException tại đây
            JOptionPane.showMessageDialog(this, "Lỗi kết nối mạng đến Server: " + ex.getMessage(), "Lỗi Kết Nối", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Hàm xử lý SỬA (CẬP NHẬT) sản phẩm ---
    private void handleUpdateProduct() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm từ bảng để sửa!");
            return;
        }
        Product p = getProductFromForm();
        if (p == null) return;
        p.setId(Integer.parseInt(txtId.getText()));

        try {
            Request req = new Request("UPDATE_PRODUCT", gson.toJson(p));
            Response res = new ServerConnection().sendRequest(req);

            if (res != null && "SUCCESS".equals(res.getStatus())) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                loadProductData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, res != null ? res.getMessage() : "Lỗi cập nhật sản phẩm!");
            }
        } catch (java.io.IOException | ClassNotFoundException ex) { // Bổ sung thêm ClassNotFoundException tại đây
            JOptionPane.showMessageDialog(this, "Lỗi kết nối mạng đến Server: " + ex.getMessage(), "Lỗi Kết Nối", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Hàm xử lý XÓA sản phẩm ---
    private void handleDeleteProduct() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần xóa từ bảng!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa sản phẩm này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String id = txtId.getText();

            try {
                Request req = new Request("DELETE_PRODUCT", id);
                Response res = new ServerConnection().sendRequest(req);

                if (res != null && "SUCCESS".equals(res.getStatus())) {
                    JOptionPane.showMessageDialog(this, "Đã xóa sản phẩm thành công!");
                    loadProductData();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa sản phẩm!", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                }
            } catch (java.io.IOException | ClassNotFoundException ex) { // Bổ sung thêm ClassNotFoundException tại đây
                JOptionPane.showMessageDialog(this, "Lỗi kết nối mạng đến Server: " + ex.getMessage(), "Lỗi Kết Nối", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Hàm phụ trợ đọc dữ liệu nhập từ các TextField
    private Product getProductFromForm() {
        try {
            String name = txtName.getText().trim();
            double price = Double.parseDouble(txtPrice.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());
            String status = cbStatus.getSelectedItem().toString();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tên sản phẩm không được trống!");
                return null;
            }
            return new Product(name, price, stock, status);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá bán và Số lượng kho phải là số hợp lệ!");
            return null;
        }
    }

    // Hàm xóa trống biểu mẫu nhập liệu
    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtPrice.setText("");
        txtStock.setText("");
        cbStatus.setSelectedIndex(0);
    }
}