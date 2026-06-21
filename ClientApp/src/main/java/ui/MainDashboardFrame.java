package ui;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.Dimension;
import java.awt.Component;
import model.User;

public class MainDashboardFrame extends javax.swing.JFrame {
    private User currentUser;
    private ProductPanel productPanel;
    private OrderPanel orderPanel;
    private ReportPanel reportPanel;

    public MainDashboardFrame(User user) {
        this.currentUser = user;
        initComponents();
        applyAuthorization();
    }

    private void applyAuthorization() {
        if (currentUser == null) return;

        if ("USER".equals(currentUser.getRole())) {
            if (productPanel != null) {
                productPanel.getBtnAdd().setEnabled(true);
                productPanel.getBtnUpdate().setEnabled(false);
                productPanel.getBtnDelete().setEnabled(false);
            }
            setTitle("Hệ thống Quản lý Kho - [Quyền: Nhân viên]");
        } else {
            setTitle("Hệ thống Quản lý Kho - [Quyền: Quản trị viên]");
        }
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    private void initComponents() {
        productPanel = new ProductPanel();
        orderPanel = new OrderPanel();
        reportPanel = new ReportPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);
    }
}