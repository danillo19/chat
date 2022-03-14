package nsu.ru.danillo19.server;

import danillo19.message.Message;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

public class Server {
    private final Integer maxServedUsersNumber = 10;
    private final Integer port;
    private final ArrayList<String> userNamesList;
    private final ArrayList<UserThread> usersThreadPool;
    private final Vector<Message> messageHistory;

    public Server(Integer port) {
        this.port = port;
        this.usersThreadPool = new ArrayList<>(maxServedUsersNumber);
        this.userNamesList = new ArrayList<>(maxServedUsersNumber);
        this.messageHistory = new Vector<>();
    }

    public void execute() {
        try (ServerSocket serverSocket = new ServerSocket(this.port)) {

            System.out.println("Server is ready\n");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                UserThread userThread = new UserThread(clientSocket, this);
                usersThreadPool.add(userThread);
                userThread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void broadcastMessage(Message message) throws IOException {
        for (UserThread thread : usersThreadPool) {
            thread.sendMessage(message);
        }
    }

    public void removeClientName(String userName) {
        userNamesList.remove(userName);
    }

    public void addUserName(String username) {
        userNamesList.add(username);
    }

    public void deleteUser(UserThread userThread) {
        usersThreadPool.remove(userThread);
    }

    public synchronized ArrayList<String> getActiveUsers() {
        return userNamesList;
    }

    public synchronized void addMessageToHistory(Message message) {
        messageHistory.add(message);
    }

    public Vector<Message> getMessageHistory() {
        return messageHistory;
    }

    public static void main(String[] args) {
        int port = 8989;
        Server server = new Server(port);
        server.execute();
    }

}
