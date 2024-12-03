import java.io.IOException;
import java.net.Socket;

public class Connection {
    private  String serverAddress;
    private int serverPort;
    public Connection(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }
    public Socket startConnection() throws IOException {
        return new Socket(serverAddress, serverPort);
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
}
