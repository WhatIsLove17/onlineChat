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
    private final JTextField fieldNickName = new JTextField("unknownUser");
    private final JTextField fieldInput = new JTextField();

    private TCPConnection connection;

    private ClientWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        area.setEditable(false);
        area.setLineWrap(true);

        fieldInput.addActionListener(this);
        add(area, BorderLayout.CENTER);
        add(fieldInput, BorderLayout.SOUTH);
        add(fieldNickName, BorderLayout.NORTH);
        setVisible(true);

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
        connection.sendString(fieldNickName.getText() + ": " + msg);
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        System.out.println("Connection is ready");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String message) {
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

    private synchronized void printMsg(String msg){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                area.append(msg + "\n");
                area.setCaretPosition(area.getDocument().getLength());
            }
        });
    }


}
