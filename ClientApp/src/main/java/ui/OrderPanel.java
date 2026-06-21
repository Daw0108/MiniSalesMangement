package ui;

import com.google.gson.Gson;
import model.Order;
import model.OrderItem;
import network.Request;
import network.Response;
import network.ServerConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class OrderPanel extends JPanel {
    private JTable cartTable;
    private DefaultTableModel cartModel;
    private JTextField txtProductId, txtQuantity, txtPrice, txtCustomerId;
    private JLabel lblTotalAmount;
    private JButton btnAddToCart, btnCheckout;
    private Gson gson = new Gson();
    private double currentTotal = 0;

    public OrderPanel() {
        setLayout(new BorderLayout());
        initUI();
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Nhập thông tin mua hàng"));

        topPanel.add(new JLabel("Mã KH (ID):")); txtCustomerId = new JTextField("1"); topPanel.add(txtCustomerId);
        topPanel.add(new JLabel("")); topPanel.add(new JLabel(""));
        topPanel.add(new JLabel("Mã Sản phẩm:")); txtProductId = new JTextField(); topPanel.add(txtProductId);
        topPanel.add(new JLabel("Số lượng:")); txtQuantity = new JTextField(); topPanel.add(txtQuantity);
        topPanel.add(new JLabel("Giá bán:")); txtPrice = new JTextField(); topPanel.add(txtPrice);

        btnAddToCart = new JButton("Thêm vào Giỏ");
        topPanel.add(btnAddToCart);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Mã SP", "Số lượng", "Đơn giá", "Thành tiền"};
        cartModel = new DefaultTableModel(columns, 0);
        cartTable = new JTable(cartModel);
        add(new JScrollPane(cartTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotalAmount = new JLabel("Tổng tiền: 0 VNĐ");
        lblTotalAmount.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotalAmount.setForeground(Color.RED);

        btnCheckout = new JButton("THANH TOÁN");
        btnCheckout.setBackground(new Color(46, 204, 113));
        btnCheckout.setForeground(Color.WHITE);

        bottomPanel.add(lblTotalAmount);
        bottomPanel.add(Box.createHorizontalStrut(20));
        bottomPanel.add(btnCheckout);
        add(bottomPanel, BorderLayout.SOUTH);

        btnAddToCart.addActionListener(e -> addToCart());
        btnCheckout.addActionListener(e -> handleCheckout());
    }

    private void addToCart() {
        try {
            int productId = Integer.parseInt(txtProductId.getText().trim());
            int quantity = Integer.parseInt(txtQuantity.getText().trim());
            double price = Double.parseDouble(txtPrice.getText().trim());

            if (quantity <= 0 || price < 0) return;

            double subTotal = quantity * price;
            currentTotal += subTotal;
            cartModel.addRow(new Object[]{productId, quantity, price, subTotal});
            lblTotalAmount.setText("Tổng tiền: " + currentTotal + " VNĐ");
            txtProductId.setText(""); txtQuantity.setText(""); txtPrice.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ!");
        }
    }

    private void handleCheckout() {
        if (cartModel.getRowCount() == 0) return;

        try {
            Order newOrder = new Order();
            newOrder.setUserId(1);
            newOrder.setCustomerId(Integer.parseInt(txtCustomerId.getText().trim()));
            newOrder.setTotalAmount(currentTotal);

            List<OrderItem> items = new ArrayList<>();
            for (int i = 0; i < cartModel.getRowCount(); i++) {
                OrderItem item = new OrderItem();
                item.setProductId((int) cartModel.getValueAt(i, 0));
                item.setQuantity((int) cartModel.getValueAt(i, 1));
                item.setPrice((double) cartModel.getValueAt(i, 2));
                items.add(item);
            }
            newOrder.setItems(items);

            btnCheckout.setEnabled(false);
            SwingWorker<Response, Void> worker = new SwingWorker<Response, Void>() {
                // Tìm đến khối doInBackground() đang gọi sendRequest bên trong OrderPanel
                @Override
                protected Response doInBackground() throws Exception {
                    // Luôn khởi tạo đối tượng qua từ khóa 'new' thay vì gọi Static trực tiếp
                    Request request = new Request("GET_ORDERS", null);
                    return new ServerConnection().sendRequest(request);
                }

                @Override
                protected void done() {
                    btnCheckout.setEnabled(true);
                    try {
                        Response response = get();
                        if ("SUCCESS".equals(response.getStatus())) {
                            JOptionPane.showMessageDialog(OrderPanel.this, "Thanh toán thành công!");
                            cartModel.setRowCount(0);
                            currentTotal = 0;
                            lblTotalAmount.setText("Tổng tiền: 0 VNĐ");
                        } else {
                            JOptionPane.showMessageDialog(OrderPanel.this, response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) { ex.printStackTrace(); }
                }
            };
            worker.execute();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi định dạng dữ liệu khách hàng!");
        }
    }
}