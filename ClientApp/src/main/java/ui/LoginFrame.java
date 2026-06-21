package ui;

import com.google.gson.Gson;
import model.User;
import network.Request;
import network.Response;
import network.ServerConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblStatus;
    private final Gson gson = new Gson();

    public LoginFrame() {
        setTitle("Đăng nhập hệ thống");
        setSize(380, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        formPanel.add(new JLabel("Tên đăng nhập:"));
        txtUsername = new JTextField();
        formPanel.add(txtUsername);

        formPanel.add(new JLabel("Mật khẩu:"));
        txtPassword = new JPasswordField();
        formPanel.add(txtPassword);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        btnLogin = new JButton("Đăng nhập");
        lblStatus = new JLabel("", SwingConstants.CENTER);
        lblStatus.setForeground(Color.RED);

        bottomPanel.add(btnLogin, BorderLayout.NORTH);
        bottomPanel.add(lblStatus, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel);

        btnLogin.addActionListener(this::handleLogin);
    }

    private void handleLogin(ActionEvent e) {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setForeground(Color.RED);
            lblStatus.setText("Vui lòng điền tài khoản và mật khẩu!");
            return;
        }

        btnLogin.setEnabled(false);
        lblStatus.setForeground(Color.BLUE);
        lblStatus.setText("Đang xác thực thông tin...");

        SwingWorker<Response, Void> worker = new SwingWorker<>() {
            @Override
            protected Response doInBackground() throws Exception {
                User credentials = new User();
                credentials.setUsername(username);
                credentials.setPasswordHash(password); // Gửi mật khẩu thô lên để Server đối chiếu jBCrypt

                Request request = new Request("LOGIN", gson.toJson(credentials));
                return new ServerConnection().sendRequest(request);
            }

            @Override
            protected void done() {
                btnLogin.setEnabled(true);
                try {
                    Response response = get();

                    if (response != null && "SUCCESS".equals(response.getStatus())) {
                        lblStatus.setForeground(new Color(0, 128, 0));
                        lblStatus.setText("Đăng nhập thành công!");

                        User loggedInUser = gson.fromJson(response.getData(), User.class);

                        // Mở màn hình chính điều khiển hệ thống
                        new MainDashboardFrame(loggedInUser).setVisible(true);
                        dispose();
                    } else {
                        lblStatus.setForeground(Color.RED);
                        lblStatus.setText(response != null ? response.getMessage() : "Sai tài khoản hoặc mật khẩu!");
                    }
                } catch (Exception ex) {
                    lblStatus.setForeground(Color.RED);
                    lblStatus.setText("Lỗi: Không thể kết nối tới máy chủ!");
                    ex.printStackTrace();
                }
            }
        };

        worker.execute();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}