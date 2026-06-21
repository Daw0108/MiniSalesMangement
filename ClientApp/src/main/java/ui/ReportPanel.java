package ui;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import network.Request;
import network.Response;
import network.ServerConnection;

import javax.swing.*;
import java.awt.*;

public class ReportPanel extends JPanel {
    private JLabel lblRevenue, lblProducts;
    private JButton btnRefresh;

    public ReportPanel() {
        setLayout(new BorderLayout());
        initUI();
        loadReportData();
    }

    private void initUI() {
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        lblRevenue = new JLabel("Tổng doanh thu: Đang tải...");
        lblRevenue.setFont(new Font("Arial", Font.BOLD, 24));
        lblRevenue.setForeground(new Color(41, 128, 185));
        lblRevenue.setHorizontalAlignment(SwingConstants.CENTER);

        lblProducts = new JLabel("Tổng sản phẩm trong kho: Đang tải...");
        lblProducts.setFont(new Font("Arial", Font.BOLD, 20));
        lblProducts.setHorizontalAlignment(SwingConstants.CENTER);

        centerPanel.add(lblRevenue);
        centerPanel.add(lblProducts);

        btnRefresh = new JButton("Cập nhật số liệu");
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(btnRefresh);

        add(new JLabel("BÁO CÁO THỐNG KÊ", SwingConstants.CENTER), BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> loadReportData());
    }

    private void loadReportData() {
        SwingWorker<Response, Void> worker = new SwingWorker<Response, Void>() {
            @Override
            protected Response doInBackground() throws Exception {
                // Sửa lỗi gọi Static và truyền đúng cấu trúc đối tượng kết nối
                Request request = new Request("GET_REPORT", null);
                return new ServerConnection().sendRequest(request);
            }

            @Override
            protected void done() {
                try {
                    Response response = get();
                    if (response != null && "SUCCESS".equals(response.getStatus())) {
                        com.google.gson.JsonObject data = com.google.gson.JsonParser.parseString(response.getData()).getAsJsonObject();
                        lblRevenue.setText(String.format("Tổng doanh thu: %,.0f VNĐ", data.get("totalRevenue").getAsDouble()));
                        lblProducts.setText("Tổng sản phẩm trong kho: " + data.get("totalProducts").getAsInt());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }
}