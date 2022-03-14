package nsu.ru.danillo19.server;

import danillo19.message.Message;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

public class UserThread extends Thread {
    private Socket socket;
    private Server server;
    private ObjectOutputStream writer;
    private final static String terminationSequence = "!quit";
    private final static String timeTemplate = "[HH : mm] ";
    private final static String usersCommand = "/users";

    private boolean checkingForTerminationMessage(String message) {
        return message.equals(terminationSequence);
    }

    public UserThread(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        this.writer = new ObjectOutputStream(socket.getOutputStream());
    }

    public void sendTerminationMessage(String username) throws IOException {
        Message finishMessage = new Message(username, terminationSequence,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(timeTemplate)));
        sendMessage(finishMessage);
    }

    public void sendMessageHistory() throws IOException {
        sendMessage(server.getMessageHistory());
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String userName = reader.readLine();

            if (checkingForTerminationMessage(userName)) {
                server.deleteUser(this);
                return;
            }

            System.out.println(userName + " connected");

            sendMessageHistory();

            Message message = new Message("Server ", userName + " connected ",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern(timeTemplate)));

            server.broadcastMessage(message);
            server.addUserName(userName);
            server.addMessageToHistory(message);


            while (true) {
                String userMessageText = reader.readLine();
                System.out.println("Getting from client: " + userMessageText);

                if (userMessageText.equals(terminationSequence)) break;
                else if (userMessageText.equals(usersCommand)) {
                    writer.writeObject(server.getActiveUsers());
                } else {
                    Message userMessage = new Message(userName, userMessageText,
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern(timeTemplate)));
                    server.broadcastMessage(userMessage);
                    server.addMessageToHistory(userMessage);
                }
            }
            sendTerminationMessage(userName);
            server.removeClientName(userName);
            server.deleteUser(this);

            Message finishInfoToAnotherClients = new Message(userName, userName + " terminated\n",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern(timeTemplate)));
            server.broadcastMessage(finishInfoToAnotherClients);

        } catch (IOException e) {
            e.printStackTrace();
            this.interrupt();
        }

    }

    public synchronized void sendMessage(Object message) throws IOException {
        writer.writeObject(message);
        writer.flush();
    }

}
