import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;

public class ClientHandler implements Runnable {
    private DataInputStream in;
    private PrintStream out;
    private String clientUsername;
    private Socket socket;
    private int serverPort;
    static ArrayList<ClientHandler> clients = new ArrayList<>();



    public ClientHandler(Socket socket, int serverPort) {
        try {
            this.serverPort = serverPort;
            this.clientUsername = "";
            this.socket = socket;
            in = new DataInputStream(socket.getInputStream());
            out = new PrintStream(socket.getOutputStream(),true);
            clients.add(this);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String jsonString = in.readLine();
                if (jsonString != null) {
                    System.out.println("Received JSON from client: " + jsonString);
                    byte[] decodedBytes = Base64.getDecoder().decode(jsonString);
                    String decodedData = new String(decodedBytes);
                    System.out.println("Decoded Data: " + decodedData);
                    // Process JSON (you can parse and manipulate the JSON here)
                    String status = Controller.manageActions(out, decodedData, serverPort, socket, in);
                    if (status.equals("true")) {
                        clients.remove(this);
                        String results = "closing";
                        String encodedData = Base64.getEncoder().encodeToString(results.getBytes());
                        out.println(encodedData);
                        socket.close();
                        break;
                    } else if (!status.equals("false")) clientUsername = status;
                }

            }

                // Echo the JSON back to the client


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }



    public static void  sendMessageToAll(String msg) {
        try{

            for (ClientHandler clientHandler : clients) {
                String encodedData = Base64.getEncoder().encodeToString(msg.getBytes());
                clientHandler.out.println(encodedData);
            }

        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    public static void  sendMessageToPrivate(String msg, String recipient) {
        try{
            for (ClientHandler clientHandler : clients) {
                if (clientHandler.clientUsername.equals(recipient)) {
                    String encodedData = Base64.getEncoder().encodeToString(msg.getBytes());
                    clientHandler.out.println(encodedData);
                }
            }

        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    public static void sendFileToPrivateUser(String recipient, Path filePath) {
        try{
            for (ClientHandler clientHandler : clients) {
                if (clientHandler.clientUsername.equals(recipient)) {
                    String encodedData = Base64.getEncoder().encodeToString("file".getBytes());
                    clientHandler.out.println(encodedData);
                    String fileName = filePath.toString();
                    FileInputStream fileInputStream = new FileInputStream(fileName);
                    DataOutputStream dataOutputStream = new DataOutputStream(clientHandler.socket.getOutputStream());

                    // Send the original file name to the server
                    dataOutputStream.writeUTF(fileName);

                    byte[] buffer = new byte[1024];
                    int bytesRead;

                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        dataOutputStream.write(buffer, 0, bytesRead);
                    }
                }
            }

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

}