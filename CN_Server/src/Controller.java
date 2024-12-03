import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;

import org.json.JSONObject;
public class Controller {
    public static String fromJSONString(String jsonString) {
        JSONObject json = new JSONObject(jsonString);
        String function = json.getString("function");
        String username = json.getString("username");
        String password = json.getString("password");
        String txt = json.getString("txt");
        String recipient = json.getString("recipient");
        return function + "," + username + "," + password + "," + txt+ "," + recipient;
    }
    public static String manageActions(PrintStream out, String json, int port, Socket socket, DataInputStream dataInputStream) throws Exception {
        String fromJson = fromJSONString(json);
        String[] action = fromJson.split(",");
        if (action[0].equals("close")) {
            DataBase.clrPortLoad(port);
            return "true";
        }
        else if (action[0].equals("getPort")) {
            int[] load = DataBase.getPortLoad();
            int minIndex = 0;
            int min = Integer.MAX_VALUE;
            for (int i = 1; i < load.length; i++) {
                if (load[i] < min) {
                    min = load[i];
                    minIndex = i;
                }
                if (min == 0) break;
            }
            String results = String.valueOf(5050+minIndex);
            String encodedData = Base64.getEncoder().encodeToString(results.getBytes());
            out.println(encodedData);
            return "true";
        }
        else if (action[0].equals("get_public_chat")) {
            String encodedData = Base64.getEncoder().encodeToString(getPublicChat().getBytes());
            out.println(encodedData);
            return "true";
        }
        else if (action[0].equals("get_private_chat")) {

            String data = getPrivateChat(action[1], action[4]);
            System.out.println(data);
            String encodedData = Base64.getEncoder().encodeToString(data.getBytes());
            out.println(encodedData);
        }
        else if (action[0].equals("FTP Private")) {
            String user = action[1];
            String recipient = action[4];
            // Read the original file name from the client
            String fileName = dataInputStream.readUTF();

            // Save the file with the original name
            Path filePath = Paths.get(fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(filePath.toString());

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = dataInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }

            fileOutputStream.close();
            sendFileToPrivateUser(recipient, filePath);
            System.out.println("file sent successfully");
            return "true";
        }
        else if (action[0].equals("user")) {
            return action[1];
        }
        else if (action[0].equals("reg")) {
            String results = json + "," + register(action[1], action[2]);
            String encodedData = Base64.getEncoder().encodeToString(results.getBytes());
            out.println(encodedData);
        }
        else if (action[0].equals("log")) {
            String results = json + "," + login(action[1], action[2]);
            String encodedData = Base64.getEncoder().encodeToString(results.getBytes());
            out.println(encodedData);
            results = "closing";
            encodedData = Base64.getEncoder().encodeToString(results.getBytes());
            out.println(encodedData);
        }
        else if (action[0].equals("send_pub")) {
            System.out.println(Arrays.toString(action));
            String results = json + "," + true;
            String encodedData = Base64.getEncoder().encodeToString(results.getBytes());
            out.println(encodedData);
            publicChat(action[1], action[3]);
        } else if (action[0].equals("send_priv")) {
            System.out.println(Arrays.toString(action));
            String results = json + "," + true;
            String encodedData = Base64.getEncoder().encodeToString(results.getBytes());
            out.println(encodedData);
            privateChat(action[1], action[4], action[3]);

        }
        else {
            String results = json + "," + false;
            String encodedData = Base64.getEncoder().encodeToString(results.getBytes());
            out.println(encodedData);
        }
        return "false";
    }

    private static boolean register(String username, String password) throws Exception {
        return DataBase.register(username,password);
    }

    private static boolean login(String username, String password) throws Exception {
        return DataBase.userLoginCheckInfo(username,password);
    }

    private static void publicChat(String username, String txt) {
        DataBase.userPublicChat(username,txt);
        sentToAllUsers(username+":"+txt);
    }

    private static String getPublicChat() {
        return DataBase.getChat();
    }
    private static String getPrivateChat(String username, String recipient) {
        return DataBase.getPrivateChat(username, recipient);
    }

    private static void sentToAllUsers(String msg) {
        ClientHandler.sendMessageToAll(msg);
    }

    private static void sentToPrivateUsers(String msg, String recipient) {
        ClientHandler.sendMessageToPrivate(msg, recipient);
    }
    private static void sendFileToPrivateUser(String recipient ,Path filePath) {
        ClientHandler.sendFileToPrivateUser(recipient,filePath);
    }

    private static void privateChat(String username, String recipient, String msg) {
        String fileName = username + "-" + recipient + ".txt";
        String fileName2 = recipient + "-" + username + ".txt";
        File file = new File(fileName);
        String chosenFileName;
        if (file.exists()) chosenFileName = fileName;
        else chosenFileName = fileName2;

        try (FileWriter fileWriter = new FileWriter(chosenFileName,true)) {
            // Write the message in the format "username:recipient"
            fileWriter.write(username + ":" + msg + System.lineSeparator());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        sentToPrivateUsers(recipient+":"+username+":"+msg, recipient);
    }
}
