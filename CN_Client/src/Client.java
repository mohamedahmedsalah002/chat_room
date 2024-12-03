import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;

public class Client{
    private final Connection connection;
    String username;


    public Client(String serverAddress, int serverPort) {
        this.connection = new Connection(serverAddress,serverPort);
        System.out.println("Connected to server...");
    }

    public void publicChat(Connection connection, String input, String username) throws IOException {
        Controller.publicChatText(connection, username, input);
    }

    public void privateChat(Connection connection, String input, String username, String recipient) throws IOException {
        Controller.privateChatText(connection,input,username,recipient);
    }
    public void sendFile(Connection connection,String recipient, String filePath, String username) throws IOException {
        Controller.sendFile(connection,recipient,filePath,username);
    }

    public void communicate(Scanner s1) {
        try {
            int port = Controller.getPort(connection);
            connection.setServerPort(port);
            while (true) {
                System.out.println("1- Login\n2- Register\n3- Exit");
                String input = s1.nextLine();
                if (input.equals("1")) {
                    System.out.println("Enter Username");
                    this.username = s1.nextLine();
                    System.out.println("Enter Password");
                    String password = s1.nextLine();
                    boolean status = Controller.login(connection, username, password);
                    //System.out.println(status);
                    Controller.getPublicChat(connection);
                    if (status) {
                        receive();
                        while (true) {
                            String msg = s1.nextLine();
                            String[] splitMsg = msg.split(":");
                            if (splitMsg[0].equals("pub")) publicChat(connection, splitMsg[1], username);
                            else if (splitMsg[0].equals("private")) privateChat(connection, splitMsg[2], username,splitMsg[1]);
                            else if (splitMsg[0].equals("file")) sendFile(connection, splitMsg[1], splitMsg[2], username);
                            else if (splitMsg[0].equals("getPrivate")) Controller.getPrivateChat(connection,splitMsg[1],username);
                            else if (msg.equals("exit chat")) {
                                break;
                            }
                        }
                    }
                    System.out.println();
                }
                else if (input.equals("2")) {
                    System.out.println("Enter Username");
                    username = s1.nextLine();
                    System.out.println("Enter Password");
                    String password = s1.nextLine();
                    Controller.register(connection,username,password);
                }
                else if (input.equals("3")) break;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


    public void receive() {
        new Thread(() -> {
            Socket server;
            DataInputStream dis;
            PrintStream writer;
            try {
                server = connection.startConnection();
                dis = new DataInputStream(server.getInputStream());
                writer = new PrintStream(server.getOutputStream(),true);
                Message msg = new Message("user",username,"","","");
                Controller.sent_text(msg.toJSONString(),writer);
                while (true) {
                    if (Controller.done) {
                        String msgReceived = dis.readLine();
                        byte[] decodedBytes = Base64.getDecoder().decode(msgReceived);
                        String decodedData = new String(decodedBytes);
                        String[] result = decodedData.split(":");
                        if (decodedData.equals("close")) {
                            Controller.terminate(server,dis,writer);
                        }
                        else if (decodedData.equals("sendingPrivateChat")) {
                            continue;
                        }
                        else if (decodedData.equals("file")) {
                            String fileName = dis.readUTF();

                            // Save the file with the original name
                            Path filePath = Paths.get(fileName);
                            FileOutputStream fileOutputStream = new FileOutputStream(filePath.toString());

                            byte[] buffer = new byte[1024];
                            int bytesRead;

                            while ((bytesRead = dis.read(buffer)) != -1) {
                                fileOutputStream.write(buffer, 0, bytesRead);
                            }

                            System.out.println("file received successfully");

                        }
                        else if (result.length == 2 ) {
                            System.out.println(decodedData);
                        }
                        else {
                            System.out.println("private:"+result[1]+":"+result[2]);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }).start();
    }



}
