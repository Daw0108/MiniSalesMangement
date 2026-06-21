package network;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerConnection {
    private static ServerConnection instance;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    // Khởi tạo Private để ngăn dùng từ khóa 'new' bên ngoài
    private ServerConnection() {
        try {
            // Kết nối đến đúng cổng 9000 của Server
            this.socket = new Socket("127.0.0.1", 9000);
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Đã kết nối tới Server thành công!");
        } catch (Exception e) {
            System.err.println("Không thể kết nối đến Server: " + e.getMessage());
        }
    }

    // Hàm getInstance tĩnh cho LoginFrame gọi đến
    public static synchronized ServerConnection getInstance() {
        if (instance == null) {
            instance = new ServerConnection();
        }
        return instance;
    }

    // Gửi yêu cầu và nhận phản hồi đồng bộ qua Socket
    public Response sendRequest(Request request) {
        try {
            out.writeObject(request);
            out.flush();
            return (Response) in.readObject();
        } catch (Exception e) {
            System.err.println("Lỗi truyền tải gói tin: " + e.getMessage());
            return new Response("ERROR", "Mất kết nối với máy chủ", null);
        }
    }
}