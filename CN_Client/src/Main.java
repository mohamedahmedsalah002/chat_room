import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Client client = new Client("localhost", 5050);
        Scanner s1 = new Scanner(System.in);
        client.communicate(s1);
    }
}