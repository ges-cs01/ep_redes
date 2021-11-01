import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    Socket clientSocket;
    ServerSocket serverSocket;

    private int port = 5050;

    public static void main(String[] arg) {
        Server server = new Server();
        server.connect();
    }

    private void connect() {
        try {
            serverSocket = new ServerSocket(port);

            System.out.println("[+] The server is online");

            while(true) {
                clientSocket = serverSocket.accept();
                ClientThread ct = new ClientThread(clientSocket);
                ct.start();
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
