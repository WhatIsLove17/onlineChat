package com.whatIsLove.chat.client;

import com.whatIsLove.network.TCPConnection;
import com.whatIsLove.network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {
    private static final String ipAddress = "192.168.1.166";
    private static final int port = 8189;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }

    private final JTextArea area = new JTextArea();
    private final JTextField fieldInput = new JTextField();
    private static String name;


    private TCPConnection connection;

    private ClientWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(false);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setBackground(new Color(55, 86, 115));
        area.setForeground(new Color(217, 235, 252));
        area.setFont(new Font(Font.DIALOG, Font.ITALIC, 15));
        fieldInput.setBackground(new Color(16, 52, 87));
        fieldInput.setPreferredSize(new Dimension(600, 30));
        fieldInput.setForeground(new Color(217, 235, 252));
        fieldInput.addActionListener(this);
        add(area, BorderLayout.CENTER);
        add(fieldInput, BorderLayout.SOUTH);
        setVisible(true);

        NameWindow nameWindow = new NameWindow();
        nameWindow.start();

        try {
            connection = new TCPConnection(this, ipAddress, port);
        } catch (IOException ex) {
            System.out.println("Connection exception: " + ex);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = fieldInput.getText();
        if (msg.equals("")) return;
        fieldInput.setText(null);
        connection.sendString(name + ": " + msg);
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        System.out.println("Connection is ready");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String message) {
        int pos = message.lastIndexOf('-');
        String isMine = "";
        if (pos != -1) isMine = message.substring(pos);
        if (isMine.equals("-mine")){
            message = message.substring(0, message.lastIndexOf('-'));
            printMyMsg(message);
        }
        else
            printMsg(message);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        System.out.println("Connection is closed");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception ex) {
        System.out.println("Connection exception: " + ex);
    }

    private synchronized void printMsg(String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                area.append(msg + "\n");
                area.setCaretPosition(area.getDocument().getLength());
            }
        });
    }

    private synchronized void printMyMsg(String msg){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                area.append(msg + "\n");
                area.setCaretPosition(area.getDocument().getLength());
            }
        });
    }

    class NameWindow extends JFrame {

        public void start() {
            setSize(300, 100);
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            setLocationRelativeTo(null);
            setResizable(false);
            setAlwaysOnTop(true);

            JTextField textField = new JTextField("Unknown user");

            JButton button = new JButton("Start chatting!");
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    name = textField.getText();
                    dispose();
                }
            });
            add(textField, BorderLayout.CENTER);
            add(button, BorderLayout.SOUTH);
            setVisible(true);
        }
    }
}
