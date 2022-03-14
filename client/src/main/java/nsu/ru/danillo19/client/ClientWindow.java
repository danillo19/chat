package nsu.ru.danillo19.client;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.regex.Pattern;

public class ClientWindow extends JFrame {
    private final BufferedWriter writerToSocket;
    private static final Integer windowHeight = 400;
    private static final Integer windowWidth = 600;
    private static final String windowTitle = "Chat";


    private JTextArea log;
    private JTextField inputField;
    private JScrollPane scrollPane;

    public ClientWindow(BufferedWriter writer) {
        this.writerToSocket = writer;
        log = new JTextArea();
        inputField = new JTextField();
        }

    public void start() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(windowWidth, windowHeight);
        this.setLocationRelativeTo(null);
        this.setTitle(windowTitle);

        Font font = new Font(" ",Font.ITALIC,14);

        log.setEditable(false);
        log.setLineWrap(true);
        log.setWrapStyleWord(true);
        log.setFont(font);

        scrollPane = new JScrollPane(log);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Chat log"));

        inputField.setBorder(BorderFactory.createTitledBorder("Message"));
        inputField.addActionListener(e -> {
            try {
                if(inputField.getText().equals("")) return;
                writerToSocket.write(inputField.getText() + "\n");
                writerToSocket.flush();
                inputField.setText(null);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        this.add(scrollPane);
        this.add(inputField, BorderLayout.SOUTH);
        setVisible(true);

    }

    public JTextArea getTextArea() {
        return this.log;
    }
}
