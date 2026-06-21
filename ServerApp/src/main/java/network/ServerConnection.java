package network;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerConnection {
    private static ServerConnection instance;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private ServerConnection() {
        try {
            this.socket = new Socket("127.0.0.1", 9000);
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Đã kết nối tới Server thành công!");
        } catch (Exception e) {
            System.err.println("Không thể kết nối đến Server: " + e.getMessage());
        }
    }

    public static synchronized ServerConnection getInstance() {
        if (instance == null) {
            instance = new ServerConnection();
        }
        return instance;
    }

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