package nsu.ru.danillo19.client;

import danillo19.message.Message;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

public class ReadingThread extends Thread {
    private final Socket socket;
    private final JTextArea log;
    private final static String terminationSequence = "!quit";

    public ReadingThread(Socket socket, JTextArea log) throws IOException {
        this.socket = socket;
        this.log = log;
    }

    public void printMessage(Message serverMessage) {
        String time = serverMessage.getSendingTime();
        String username = serverMessage.getUsername();
        String text = serverMessage.getText();

        log.append(time + " " + username +  " :~$ " + text + "\n");

        log.setCaretPosition(log.getDocument().getLength());
    }

    public void printActiveUsers(ArrayList<String> users) {
        String outputMessage = String.join("," , users);
        JOptionPane.showMessageDialog(null, "Users: " + outputMessage);
    }

    public void printMessageHistory(Vector<Message> history) {
        for (Message message : history) {
            printMessage(message);
        }
    }

    @Override
    public void run() {
        try (ObjectInputStream clientReader = new ObjectInputStream(socket.getInputStream())) {
            while (true) {
                try {
                    Object serverMessage = clientReader.readObject();
                    if (serverMessage instanceof Message) {
                        if ((((Message) serverMessage).getText()).equals(terminationSequence)) break;
                        printMessage((Message) serverMessage);
                    } else if (serverMessage instanceof ArrayList) {
                        ArrayList<String> userNameList = (ArrayList<String>) serverMessage;
                        printActiveUsers(userNameList);
                    } else if (serverMessage instanceof Vector) {
                        Vector<Message> messageHistory = (Vector<Message>) serverMessage;
                        printMessageHistory(messageHistory);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
