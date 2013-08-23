package com.kodingen.cetrin.player;

import com.kodingen.cetrin.model.Move;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkPlayer extends Player {
    public static final int SERVER = 0;
    public static final int CLIENT = 1;
    public static final int PORT = 8001;

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean firstTurn = false;

    public NetworkPlayer(int symbol, int type, String addr) throws IOException {
        super(symbol, false);
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
    public Move getMove() throws IOException {
        try {
            if (!firstTurn) { // nothing to send, only receive move on first turn
                send(new Message(Message.NEW_TURN, gm.getLastMove()));
            } else {
                firstTurn = false;
            }
            if (gm.hasWinner()) return null;
            Message data = (Message) in.readObject();
            switch (data.getCode()) {
                case Message.NEW_TURN:
                    return new Move(data.getMove().getX(), data.getMove().getY());
            }
        } catch (IOException e) {
            throw new IOException("Connection lost.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
