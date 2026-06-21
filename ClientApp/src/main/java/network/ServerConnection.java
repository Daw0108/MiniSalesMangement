package network;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerConnection {
    private final String SERVER_HOST = "127.0.0.1";
    private final int SERVER_PORT = 9000;

    public ServerConnection() {}

    public Response sendRequest(Request request) throws java.io.IOException, ClassNotFoundException {
        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);

            out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(request);
            out.flush();

            in = new ObjectInputStream(socket.getInputStream());
            Object responseObj = in.readObject();

            if (responseObj instanceof Response) {
                return (Response) responseObj;
            }
            return new Response("ERROR", "Phản hồi sai định dạng!", null);

        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (Exception e) {
                System.err.println("Lỗi đóng socket: " + e.getMessage());
            }
        }
    }
}