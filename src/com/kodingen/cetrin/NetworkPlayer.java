package com.kodingen.cetrin;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkPlayer extends Player {
    public static final int SERVER = 0;
    public static final int CLIENT = 1;
    public static final int PORT = 8001;

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean firstTurn = false;

    private NetworkPlayer(int symbol, boolean showInputForm) {
        super(symbol, showInputForm);
    }

    public NetworkPlayer(int symbol, int type, String addr) throws IOException {
        this(symbol, false);
        switch (type) {
            case SERVER:
                ServerSocket listener = new ServerSocket(PORT);
                Socket s = listener.accept();
                in = new ObjectInputStream(new BufferedInputStream(s.getInputStream()));
                out = new ObjectOutputStream(new BufferedOutputStream(s.getOutputStream()));
                out.flush();
                break;
            case CLIENT:
                firstTurn = true;
                s = new Socket(addr, PORT);
                out = new ObjectOutputStream(new BufferedOutputStream(s.getOutputStream()));
                out.flush();
                in = new ObjectInputStream(new BufferedInputStream(s.getInputStream()));
                break;
            default:
                throw new IllegalArgumentException("Wrong game mode!");
        }
    }

    private void send(Message data) throws IOException {
        out.writeObject(data);
        out.flush();
    }

    @Override
    public void makeMove() throws IOException {
        try {
            if (!firstTurn) {
                send(new Message(Message.NEW_TURN, gm.getLastTurn()));
            } else {
                firstTurn = false;
            }
            if (gm.hasWinner()) return;
            Message data = (Message) in.readObject();
            switch (data.getCode()) {
                case Message.NEW_TURN:
                    gm.makeMove(data.getTurn().getX(), data.getTurn().getY());
            }
        } catch (IOException e) {
            throw new IOException("Connection lost.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
