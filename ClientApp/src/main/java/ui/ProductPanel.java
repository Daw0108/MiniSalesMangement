package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Product;
import network.Request;
import network.Response;
import network.ServerConnection;

public class ProductPanel extends JPanel {
    private final Gson gson = new Gson();
    private DefaultTableModel tableModel;

    private JTextField txtId, txtName, txtPrice, txtStock;
    private JComboBox<String> cbStatus;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    private JTable productTable;

    public ProductPanel() {
        initComponents();
        loadProductData();
    }

    private void handleAddProduct() {
        Product p = getProductFromForm();
        if (p == null) return;

        try {
            Request req = new Request("ADD_PRODUCT", gson.toJson(p));
            Response res = new ServerConnection().sendRequest(req);

            if (res != null && "SUCCESS".equals(res.getStatus())) {
                JOptionPane.showMessageDialog(this, "Thêm sản phẩm thành công!");
                loadProductData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi thêm sản phẩm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối mạng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

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
                JOptionPane.showMessageDialog(this, "Cập nhật sản phẩm thành công!");
                loadProductData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật sản phẩm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối mạng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteProduct() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần xóa trên bảng!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn chắc chắn muốn xóa sản phẩm này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Request req = new Request("DELETE_PRODUCT", txtId.getText());
                Response res = new ServerConnection().sendRequest(req);

                if (res != null && "SUCCESS".equals(res.getStatus())) {
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
                    loadProductData();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa sản phẩm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi kết nối mạng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadProductData() {
        try {
            Request req = new Request("GET_ALL_PRODUCTS", null);
            Response res = new ServerConnection().sendRequest(req);

            if (res != null && "SUCCESS".equals(res.getStatus())) {
                List<Product> list = gson.fromJson(res.getData(), new TypeToken<List<Product>>(){}.getType());
                tableModel.setRowCount(0);
                for (Product p : list) {
                    tableModel.addRow(new Object[]{p.getId(), p.getName(), p.getPrice(), p.getStock(), p.getStatus()});
                }
            }
        } catch (Exception ex) {
            System.err.println("Không thể tải danh sách sản phẩm: " + ex.getMessage());
        }
    }

    private Product getProductFromForm() {
        try {
            String name = txtName.getText().trim();
            double price = Double.parseDouble(txtPrice.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());
            String status = cbStatus.getSelectedItem().toString();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tên sản phẩm không được để trống!");
                return null;
            }

            return new Product(name, price, stock, status);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá bán và Số lượng kho phải nhập số hợp lệ!", "Lỗi định dạng", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtPrice.setText("");
        txtStock.setText("");
        cbStatus.setSelectedIndex(0);
        productTable.clearSelection();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel panelForm = new JPanel(new GridLayout(5, 2, 5, 5));
        panelForm.setBorder(BorderFactory.createTitledBorder("Thông tin sản phẩm"));

        panelForm.add(new JLabel("Mã SP (Ẩn):"));
        txtId = new JTextField();
        txtId.setEditable(false);
        panelForm.add(txtId);

        panelForm.add(new JLabel("Tên sản phẩm:"));
        txtName = new JTextField();
        panelForm.add(txtName);

        panelForm.add(new JLabel("Giá bán (VND):"));
        txtPrice = new JTextField();
        panelForm.add(txtPrice);

        panelForm.add(new JLabel("Số lượng kho:"));
        txtStock = new JTextField();
        panelForm.add(txtStock);

        panelForm.add(new JLabel("Trạng thái:"));
        cbStatus = new JComboBox<>(new String[]{"Còn hàng", "Hết hàng", "Ngừng kinh doanh"});
        panelForm.add(cbStatus);

        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        btnAdd = new JButton("Thêm mới");
        btnUpdate = new JButton("Cập nhật");
        btnDelete = new JButton("Xóa bỏ");
        btnClear = new JButton("Làm mới");

        panelButtons.add(btnAdd);
        panelButtons.add(btnUpdate);
        panelButtons.add(btnDelete);
        panelButtons.add(btnClear);

        JPanel leftContainer = new JPanel(new BorderLayout());
        leftContainer.add(panelForm, BorderLayout.CENTER);
        leftContainer.add(panelButtons, BorderLayout.SOUTH);
        add(leftContainer, BorderLayout.WEST);

        String[] columns = {"Mã sản phẩm", "Tên sản phẩm", "Giá bán", "Số lượng", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        productTable = new JTable(tableModel);
        add(new JScrollPane(productTable), BorderLayout.CENTER);

        productTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = productTable.getSelectedRow();
            if (selectedRow >= 0) {
                txtId.setText(tableModel.getValueAt(selectedRow, 0).toString());
                txtName.setText(tableModel.getValueAt(selectedRow, 1).toString());
                txtPrice.setText(tableModel.getValueAt(selectedRow, 2).toString());
                txtStock.setText(tableModel.getValueAt(selectedRow, 3).toString());
                cbStatus.setSelectedItem(tableModel.getValueAt(selectedRow, 4).toString());
            }
        });

        btnAdd.addActionListener(e -> handleAddProduct());
        btnUpdate.addActionListener(e -> handleUpdateProduct());
        btnDelete.addActionListener(e -> handleDeleteProduct());
        btnClear.addActionListener(e -> clearForm());
    }

    public JButton getBtnAdd() { return btnAdd; }
    public JButton getBtnUpdate() { return btnUpdate; }
    public JButton getBtnDelete() { return btnDelete; }
}