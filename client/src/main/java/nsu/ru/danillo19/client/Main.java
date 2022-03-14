package nsu.ru.danillo19.client;

import javax.swing.*;

public class Main {
    private final static String hostname = "localhost";

    public static void main(String[] args) {
        Integer port = 8989;
        Client client = new Client(hostname,port);
        client.execute();
    }
}
