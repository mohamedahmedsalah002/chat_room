import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class PortListener implements Runnable{
    private ServerSocket serverPort;
    int port;

    public PortListener(ServerSocket serverPort, int port) {
        this.serverPort = serverPort;
        this.port = port;
    }

    public void run() {
        try {
            while (true) {

                Socket clientSocket = serverPort.accept();
                System.out.println("Number of Running Connections: " + clientSocket.getInetAddress() + " on port " + port);
                DataBase.setPortLoad(port);
                ClientHandler ch = new ClientHandler(clientSocket, port);
                // Create a new thread for each client
                Thread clientThread = new Thread(ch);
                clientThread.start();
                System.out.println("clients  : " + ClientHandler.clients.size());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
