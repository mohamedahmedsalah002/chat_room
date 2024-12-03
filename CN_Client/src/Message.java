import org.json.JSONObject;
public class Message {

    private final String function;
    private final String username;
    private final String password;
    private final String txt;
    private final String recipient;

    public Message(String function, String username, String password, String txt, String recipient) {
        this.function = function;
        this.username = username;
        this.password = password;
        this.txt = txt;
        this.recipient = recipient;
    }

    public String toJSONString() {
        JSONObject json = new JSONObject();
        json.put("function", function);
        json.put("username", username);
        json.put("password", password);
        json.put("txt", txt);
        json.put("recipient", recipient);
        return json.toString();
    }

}