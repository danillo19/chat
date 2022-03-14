package danillo19.message;

import java.io.Serializable;

public class Message implements Serializable {
    private String username;
    private String text;
    private String sendingTime;

    public Message(String username, String text, String sendingTime) {
        this.username = username;
        this.text = text;
        this.sendingTime = sendingTime;
    }

    public String getUsername() {
        return this.username;
    }

    public String getSendingTime() {
        return this.sendingTime;
    }

    public String getText() {
        return this.text;
    }


}
