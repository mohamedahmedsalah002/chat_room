import java.io.*;
import java.sql.*;

public class DataBase {


    private static DataBase instance;
    private static Connection connection;

    private static int[] portLoad;

    public static DataBase getInstance() {
        if (instance == null) {
            instance = new DataBase();
        }
        return instance;
    }

    public Connection connectDB(int size) {
        portLoad = new int[size];
        for (int i = 0; i < size; i++) {
            portLoad[i] = 0;
        }
        try {
            if (connection == null || connection.isClosed()) {

                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/authentication","root","root");
            }
            return connection;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }



    public static boolean register( String username , String password) throws Exception {
        try {

            PreparedStatement ps = connection.prepareStatement("SELECT * FROM login WHERE username = ? "); // check for if the  id is already in database
            ps.setString(1, username);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.isBeforeFirst()) {
                return false;
            } else {

                ps = connection.prepareStatement("INSERT INTO login (username, password) VALUES (?, ?)");
                ps.setString(1, username);
                ps.setString(2, password);
                ps.executeUpdate();
                return true;

            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static boolean userLoginCheckInfo(String username , String password) throws SQLException {
        try {

            PreparedStatement ps = connection.prepareStatement("SELECT * FROM login WHERE username = ? AND password = ?");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet resultSet = ps.executeQuery();
            return resultSet.next();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static void userPublicChat(String username , String txt) {
        // Specify the file name
        String fileName = "publicChat.txt";

        try (FileWriter fileWriter = new FileWriter(fileName, true)) {
            // Append the message to the file
            fileWriter.write(username+":"+txt + System.lineSeparator());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static String getChat() {
        // Specify the file name
        String fileName = "publicChat.txt";

        StringBuilder content = new StringBuilder();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            // Handle the case where the file doesn't exist
            System.out.println(e.getMessage());
        }

        return content.toString();
    }

    public static String getPrivateChat(String username, String recipient) {
        String fileName = username + "-" + recipient + ".txt";
        String fileName2 = recipient + "-" + username + ".txt";
        File file = new File(fileName);
        String chosenFileName;
        if (file.exists()) chosenFileName = fileName;
        else chosenFileName = fileName2;
        StringBuilder content = new StringBuilder();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(chosenFileName))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            // Handle the case where the file doesn't exist
            System.out.println(e.getMessage());
        }

        return content.toString();
    }

    public static void setPortLoad(int port) {
        DataBase.portLoad[port-5050] = ++DataBase.portLoad[port-5050];
    }
    public static void clrPortLoad(int port) {
        DataBase.portLoad[port-5050] = --DataBase.portLoad[port-5050];
    }

    public static int[] getPortLoad() {
        return portLoad;
    }
}
