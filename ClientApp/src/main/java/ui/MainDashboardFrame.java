package ui;

import model.User;

import javax.swing.*;
import java.awt.*;

public class MainDashboardFrame extends JFrame {
    private User currentUser;
    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    public MainDashboardFrame(User user) {
        this.currentUser = user;
        setTitle("Phần mềm Quản lý Bán hàng - Nhân viên: " + user.getUsername());
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(200);

        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));

        JButton btnProduct = createMenuButton("Quản lý Sản phẩm");
        JButton btnOrder = createMenuButton("Bán hàng (POS)");
        JButton btnReport = createMenuButton("Báo cáo Thống kê");
        JButton btnLogout = createMenuButton("Đăng xuất");

        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebarPanel.add(btnProduct);
        sidebarPanel.add(btnOrder);
        sidebarPanel.add(btnReport);
        sidebarPanel.add(Box.createVerticalGlue());
        sidebarPanel.add(btnLogout);

        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);

        // Nạp tất cả các Panel vào bộ nhớ
        mainContentPanel.add(new JPanel(), "WELCOME_PANEL");
        mainContentPanel.add(new ProductPanel(), "PRODUCT_PANEL");
        mainContentPanel.add(new OrderPanel(), "ORDER_PANEL");
        mainContentPanel.add(new ReportPanel(), "REPORT_PANEL");

        splitPane.setLeftComponent(sidebarPanel);
        splitPane.setRightComponent(mainContentPanel);
        add(splitPane);

        // Xử lý chuyển đổi giao diện khi bấm Menu
        btnProduct.addActionListener(e -> cardLayout.show(mainContentPanel, "PRODUCT_PANEL"));
        btnOrder.addActionListener(e -> cardLayout.show(mainContentPanel, "ORDER_PANEL"));
        btnReport.addActionListener(e -> cardLayout.show(mainContentPanel, "REPORT_PANEL"));
        btnLogout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }
}