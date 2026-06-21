package network;

import com.google.gson.Gson;
import dao.ProductDAO;
import dao.UserDAO;
import model.Product;
import model.User;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final Gson gson = new Gson();

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            // Khởi tạo luồng nhận và gửi đối tượng qua Socket
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            System.out.println("[+] Client kết nối thành công: " + socket.getInetAddress());

            Object input;
            // Vòng lặp liên tục lắng nghe các Request gửi lên từ Client
            while ((input = in.readObject()) != null) {
                if (input instanceof Request) {
                    Request request = (Request) input;
                    Response response = handleRequest(request);

                    // Gửi phản hồi ngược lại cho Client
                    out.writeObject(response);
                    out.flush();
                }
            }
        } catch (Exception e) {
            System.out.println("[-] Client đã ngắt kết nối hoặc có lỗi luồng: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    // --- HÀM ĐIỀU PHỐI VÀ XỬ LÝ REQUEST CHÍNH ---
    private Response handleRequest(Request request) {
        String action = request.getAction();
        String dataJson = request.getData();

        switch (action) {
            case "LOGIN":
                return handleLogin(dataJson);

            case "GET_PRODUCTS":
                return handleGetProducts();

            case "ADD_PRODUCT":
                return handleAddProduct(dataJson);

            case "UPDATE_PRODUCT":
                return handleUpdateProduct(dataJson);

            case "DELETE_PRODUCT":
                return handleDeleteProduct(dataJson);

            default:
                return new Response("ERROR", "Hành động (Action) không được hỗ trợ!", null);
        }
    }

    // 1. Xử lý Đăng nhập (So sánh chuỗi thô trực tiếp để đảm bảo thông suốt)
    private Response handleLogin(String loginDataJson) {
        try {
            User credentials = gson.fromJson(loginDataJson, User.class);
            UserDAO userDAO = new UserDAO();
            User dbUser = userDAO.findByUsername(credentials.getUsername());

            if (dbUser != null) {
                // So sánh bằng equals() chữ thô trực tiếp theo giải pháp thông suốt hệ thống
                boolean isPasswordMatch = credentials.getPasswordHash().equals(dbUser.getPasswordHash());

                if (isPasswordMatch) {
                    System.out.println(">> [SUCCESS] Đăng nhập thành công: " + dbUser.getUsername());
                    dbUser.setPasswordHash(""); // Bảo mật chuỗi mật khẩu
                    return new Response("SUCCESS", "Đăng nhập thành công!", gson.toJson(dbUser));
                }
            }
            System.out.println(">> [FAILED] Đăng nhập thất bại: Sai tài khoản hoặc mật khẩu.");
            return new Response("ERROR", "Sai tên đăng nhập hoặc mật khẩu", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response("ERROR", "Lỗi xử lý đăng nhập hệ thống", null);
        }
    }

    // 2. Xử lý lấy danh sách toàn bộ sản phẩm
    private Response handleGetProducts() {
        try {
            ProductDAO productDAO = new ProductDAO();
            java.util.List<Product> list = productDAO.findAll();
            return new Response("SUCCESS", "Lấy danh sách sản phẩm thành công!", gson.toJson(list));
        } catch (Exception e) {
            return new Response("ERROR", "Lỗi tải dữ liệu kho: " + e.getMessage(), null);
        }
    }

    // 3. Xử lý Thêm mới sản phẩm vào kho
    private Response handleAddProduct(String productJson) {
        try {
            Product product = gson.fromJson(productJson, Product.class);
            ProductDAO productDAO = new ProductDAO();
            boolean success = productDAO.insert(product);

            if (success) {
                System.out.println(">> Đã thêm thành công sản phẩm mới: " + product.getName());
                return new Response("SUCCESS", "Thêm sản phẩm thành công!", null);
            }
            return new Response("ERROR", "Không thể thêm sản phẩm vào Database!", null);
        } catch (Exception e) {
            return new Response("ERROR", "Lỗi xử lý thêm sản phẩm: " + e.getMessage(), null);
        }
    }

    // 4. Xử lý HOÀN THIỆN phần CẬP NHẬT (SỬA) sản phẩm đã có trong kho
    private Response handleUpdateProduct(String productJson) {
        try {
            Product productToUpdate = gson.fromJson(productJson, Product.class);
            ProductDAO productDAO = new ProductDAO();
            boolean success = productDAO.update(productToUpdate);

            if (success) {
                System.out.println(">> Cập nhật thành công sản phẩm ID: " + productToUpdate.getId());
                return new Response("SUCCESS", "Cập nhật sản phẩm thành công!", null);
            }
            return new Response("ERROR", "Cập nhật thất bại! Vui lòng kiểm tra lại thông tin.", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response("ERROR", "Lỗi hệ thống khi sửa sản phẩm: " + e.getMessage(), null);
        }
    }

    // 5. Xử lý HOÀN THIỆN phần XÓA sản phẩm ra khỏi kho
    private Response handleDeleteProduct(String productIdStr) {
        try {
            int productId = Integer.parseInt(productIdStr);
            ProductDAO productDAO = new ProductDAO();
            boolean success = productDAO.delete(productId);

            if (success) {
                System.out.println(">> Đã xóa thành công sản phẩm ID: " + productId);
                return new Response("SUCCESS", "Xóa sản phẩm thành công!", null);
            }
            return new Response("ERROR", "Không thể xóa! Sản phẩm này có thể đang tồn tại trong hóa đơn bán hàng.", null);
        } catch (Exception e) {
            return new Response("ERROR", "Lỗi hệ thống khi xóa sản phẩm: " + e.getMessage(), null);
        }
    }

    // Đóng Socket an toàn khi ngắt kết nối
    private void closeConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (Exception e) {
            System.err.println("Lỗi đóng kết nối: " + e.getMessage());
        }
    }
}