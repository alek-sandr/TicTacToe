package com.kodingen.cetrin.player;

import com.kodingen.cetrin.model.Move;

import java.io.Serializable;

class Message implements Serializable {
    private final Move move;
    private final int code;
    public static final int NEW_TURN = 0;

    Message(int code) {
        this(code, null);
    }

    Message(int code, Move move) {
        this.code = code;
        this.move = move;
    }

    public int getCode() {
        return code;
    }

    public Move getMove() {
        return move;
    }
}