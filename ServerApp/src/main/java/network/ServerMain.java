package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerMain {
    private static final int PORT = 9000;
    private static ExecutorService threadPool = Executors.newFixedThreadPool(20);

    public static void main(String[] args) {
        System.out.println("=== KHỞI ĐỘNG SERVER BÁN HÀNG ===");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server đang lắng nghe trên cổng " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[+] Có kết nối mới từ: " + clientSocket.getInetAddress());

                threadPool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Lỗi khởi tạo Server: " + e.getMessage());
        }
    }
}