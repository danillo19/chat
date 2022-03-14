package nsu.ru.danillo19.client;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

public class Client {
    private final String hostname;
    private final Integer port;
    private BufferedWriter writerToSocket;
    private final static String terminationSequence = "!quit";
    private final static String welcomeMessage = "Your name : ";
    private static final String regexTemplate = "[A-Za-z0-9А-Яа-я]+";
    private static final String illegalUsernameMessage = "Bad username. Try again";

    public Client(String hostname, Integer port) {
        this.hostname = hostname;
        this.port = port;
    }



    public String getUsername() {
        String username = JOptionPane.showInputDialog(welcomeMessage);
        System.out.println(username);
        if (username == null) {
            return null;
        }

        if (!Pattern.matches(regexTemplate, username)) {
            JOptionPane.showMessageDialog(null, illegalUsernameMessage);
            getUsername();
        }
        return username;
    }

    public void sendTerminationSequence() throws IOException {
        writerToSocket.write(terminationSequence + "\n");
        writerToSocket.flush();
    }

    public boolean checkingForClosingInputPane(String username) {
        return username == null;
    }

    public void execute() {
        try {
            Socket socket = new Socket(hostname, port);
            writerToSocket = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String username = getUsername();
            if(checkingForClosingInputPane(username)) {
                sendTerminationSequence();
                return;
            }
            sendUsername(username);

            ClientWindow clientWindow = new ClientWindow(writerToSocket);
            clientWindow.start();

            ReadingThread readingThread = new ReadingThread(socket, clientWindow.getTextArea());
            readingThread.start();

            clientWindow.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    try {
                        writerToSocket.write(terminationSequence + "\n");
                        writerToSocket.flush();

                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    super.windowClosing(e);
                }
            });


            readingThread.join();
            writerToSocket.close();
        } catch (UnknownHostException | InterruptedException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

    public void sendUsername(String username) throws IOException {
        writerToSocket.write(username + "\n");
        writerToSocket.flush();
    }


}
