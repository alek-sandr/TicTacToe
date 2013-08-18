package com.kodingen.cetrin;

import java.io.Serializable;

class Message implements Serializable {
    private final int x, y;
    private final int code;
    public static final int NEW_TURN = 0;

    Message(int code) {
        this(code, 0, 0);
    }

    Message(int code, int x, int y) {
        this.code = code;
        this.x = x;
        this.y = y;
    }

    public int getCode() {
        return code;
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
}