package network;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerConnection {
    private final String SERVER_HOST = "127.0.0.1";
    private final int SERVER_PORT = 9000;

    public ServerConnection() {
    }

    // Hàm gửi Request và nhận Response đồng bộ, chuẩn Object Stream
    public Response sendRequest(Request request) throws java.io.IOException, ClassNotFoundException {
        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        try {
            // 1. Kết nối tới Server
            socket = new Socket(SERVER_HOST, SERVER_PORT);

            // 2. KHỞI TẠO LUỒNG GHI (OUTPUT) TRƯỚC
            out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(request);
            out.flush();

            // 3. KHỞI TẠO LUỒNG ĐỌC (INPUT) SAU
            in = new ObjectInputStream(socket.getInputStream());
            Object responseObj = in.readObject();

            if (responseObj instanceof Response) {
                return (Response) responseObj;
            }
            return new Response("ERROR", "Phản hồi từ Server không đúng định dạng!", null);

        } finally {
            // Tự động dọn dẹp và giải phóng tài nguyên hệ thống sau khi gửi xong
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (Exception e) {
                System.err.println("Lỗi đóng Socket: " + e.getMessage());
            }
        }
    }
}