import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.Socket;
import java.util.Base64;

public class Controller {

    public static boolean done = true;
    private static final String close = "close";

    public static void sent_text(String text, PrintStream writer) throws IOException {
        String encodedData = Base64.getEncoder().encodeToString(text.getBytes());
        writer.println(encodedData);
        //System.out.println("Sent JSON to server: " + text);
    }

    public static String receive_text(DataInputStream reader) throws IOException {
        String echoedJson = reader.readLine();
        byte[] decodedBytes = Base64.getDecoder().decode(echoedJson);
        String decodedData = new String(decodedBytes);
        //System.out.println("Received echoed JSON from server: " + echoedJson);
        //System.out.println("decoded data: " +decodedData);
        return decodedData;
    }

    public static void terminate(Socket socket,DataInputStream reader,PrintStream writer) throws IOException {
        Message msg = new Message(close,"", "","","");
        sent_text(msg.toJSONString(), writer);
        receive_text(reader);
        reader.close();
        writer.close();
        socket.close();
    }

    public static int getPort(Connection connection) throws IOException {
        Socket socket = connection.startConnection();


        DataInputStream reader = new DataInputStream(socket.getInputStream());
        PrintStream writer = new PrintStream(socket.getOutputStream(),true);
        Message msg = new Message("getPort","", "","","");
        sent_text(msg.toJSONString(), writer);
        int port = Integer.parseInt(receive_text(reader));
        reader.close();
        writer.close();
        socket.close();
        return port;
    }

    public static void register(Connection connection, String username, String password) throws IOException {
        Socket socket = connection.startConnection();

        DataInputStream reader = new DataInputStream(socket.getInputStream());
        PrintStream writer = new PrintStream(socket.getOutputStream(),true);
        Message msg = new Message("reg",username,password, "","");
        sent_text(msg.toJSONString(), writer);
        done = false;
        String[] results = receive_text(reader).split(",");
        done = true;
        int len = results.length;
        if (results[len-1].equals("true")) System.out.println("Registration Successful");
        else System.out.println("Registration Failed");
        terminate(socket,reader,writer);
    }

    public static Boolean login(Connection connection, String username, String password) throws IOException {
        Socket socket = connection.startConnection();

        DataInputStream reader = new DataInputStream(socket.getInputStream());
        PrintStream writer = new PrintStream(socket.getOutputStream(),true);
        Message msg = new Message("log",username,password, "","");
        sent_text(msg.toJSONString(), writer);
        String[] results = receive_text(reader).split(",");
        terminate(socket,reader,writer);

        int len = results.length;
        if (results[len-1].equals("true")) {
            System.out.println("Login Successful");
            return true;
        }
        else {
            System.out.println("Login Failed");
            return false;
        }

    }
    public static void publicChatText(Connection connection, String username, String txt) throws IOException {
        Socket socket = connection.startConnection();

        DataInputStream reader = new DataInputStream(socket.getInputStream());
        PrintStream writer = new PrintStream(socket.getOutputStream(),true);

        Message msg = new Message("send_pub",username, "",txt,"");
        sent_text(msg.toJSONString(), writer);
        String[] results;

        done = false;
        results = receive_text(reader).split(",");
        done = true;
        int len = results.length;
        if (results[len-1].equals("true")) {
            //System.out.println("msg sent Successful");
        }
        else System.out.println("msg sent Failed");
        terminate(socket,reader,writer);

    }

    public static void privateChatText(Connection connection, String input, String username, String recipient) throws IOException {
        Socket socket = connection.startConnection();
        DataInputStream reader = new DataInputStream(socket.getInputStream());
        PrintStream writer = new PrintStream(socket.getOutputStream(),true);
        Message msg = new Message("send_priv",username, "",input,recipient);
        sent_text(msg.toJSONString(), writer);
        String[] results;
        done = false;
        results = receive_text(reader).split(",");
        done = true;
        int len = results.length;
        if (results[len-1].equals("true")) {
            //System.out.println("msg sent Successful");
        }
        else System.out.println("msg sent Failed");
        terminate(socket,reader,writer);
    }

    public static void sendFile(Connection connection,String recipient, String filePath, String username) throws IOException {
        Socket socket = connection.startConnection();
        PrintStream writer = new PrintStream(socket.getOutputStream(),true);
        Message msg = new Message("FTP Private",username, "","",recipient);
        sent_text(msg.toJSONString(), writer);



        File fileToSend = new File(filePath);
        String fileName = fileToSend.getName(); // Get the original file name

        FileInputStream fileInputStream = new FileInputStream(fileToSend);
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

        // Send the original file name to the server
        dataOutputStream.writeUTF(fileName);

        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            dataOutputStream.write(buffer, 0, bytesRead);
        }

        writer.close();
        fileInputStream.close();
        dataOutputStream.close();
        socket.close();

        System.out.println("File sent successfully.");
    }

    public static void getPublicChat(Connection connection) throws IOException {
        Socket socket = connection.startConnection();
        DataInputStream reader = new DataInputStream(socket.getInputStream());
        PrintStream writer = new PrintStream(socket.getOutputStream(),true);
        Message msg = new Message("get_public_chat","", "","","");
        sent_text(msg.toJSONString(), writer);
        System.out.println(receive_text(reader));
    }

    public static void getPrivateChat(Connection connection, String recipient, String username) throws IOException {
        Socket socket = connection.startConnection();
        DataInputStream reader = new DataInputStream(socket.getInputStream());
        PrintStream writer = new PrintStream(socket.getOutputStream(),true);

        Message msg = new Message("get_private_chat", username, "","",recipient);
        sent_text(msg.toJSONString(), writer);
        String results;

        done = false;
        results = receive_text(reader);
        done = true;
        System.out.println(results);
        terminate(socket,reader,writer);

    }

}
