package com.kodingen.cetrin.model;

import java.io.Serializable;

public class Move implements Serializable {
    public static final int FIELD_SIZE = GameModel.FIELD_SIZE;
    private int x, y;

    public Move(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public boolean isCenterMove() {
        return x == FIELD_SIZE / 2 && y == FIELD_SIZE / 2;
    }
    public boolean isCornerMove() {
        return (x == 0 && y == 0) ||
                (x == 0 && y == FIELD_SIZE - 1) ||
                (x == FIELD_SIZE - 1 && y == 0) ||
                (x == FIELD_SIZE - 1 && y == FIELD_SIZE - 1);
    }
    public boolean isSideMove() {
        return (x == FIELD_SIZE / 2 && y == 0) ||
                (x == 0 && y == FIELD_SIZE / 2) ||
                (x == FIELD_SIZE - 1 && y == FIELD_SIZE / 2) ||
                (x == FIELD_SIZE / 2 && y == FIELD_SIZE - 1);
    }
}
