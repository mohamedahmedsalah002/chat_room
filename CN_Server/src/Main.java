public class Main {
    public static void main(String[] args) {
        DataBase db = DataBase.getInstance();
        int[] ports = {5050,5051,5052,5053,5054};
        db.connectDB(ports.length);
        new Server(ports);
    }
}