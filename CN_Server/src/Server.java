import java.io.*;
import java.net.ServerSocket;

public class Server {

    private ServerSocket[] serverSocket;
    public Server(int[] port) {
        try {
            serverSocket = new ServerSocket[port.length];
            for (int i = 0; i < port.length; i++) {
                serverSocket[i] = new ServerSocket(port[i]);
                System.out.println("Server listening on port " + port[i]);
                PortListener portListener = new PortListener(serverSocket[i], port[i]);
                Thread portThread = new Thread(portListener);
                portThread.start();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }




}
