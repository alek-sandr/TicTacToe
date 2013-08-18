package com.kodingen.cetrin;

import java.io.Serializable;
import static com.kodingen.cetrin.GameModel.Turn;

class Message implements Serializable {
    private final Turn turn;
    private final int code;
    public static final int NEW_TURN = 0;

    Message(int code) {
        this(code, null);
    }

    Message(int code, Turn turn) {
        this.code = code;
        this.turn = turn;
    }

    public int getCode() {
        return code;
    }

    public Turn getTurn() {
        return turn;
    }
}