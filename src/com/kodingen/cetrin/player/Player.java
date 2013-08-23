package com.kodingen.cetrin.player;

import com.kodingen.cetrin.model.GameModel;
import com.kodingen.cetrin.model.Move;

import java.io.IOException;

public abstract class Player {
    private final int symbol;
    private boolean showInput;
    protected GameModel gm;

    public Player(int symbol, boolean showInputForm) {
        this.symbol = symbol;
        showInput = showInputForm;
    }

    public void setGameModel(GameModel gm) {
        this.gm = gm;
    }

    public int getSymbolCode() {
        return symbol;
    }

    public char getSymbol() {
        return symbol == GameModel.X ? 'X' : 'O';
    }

    public abstract Move getMove() throws IOException;

    public boolean showInputForm() {
        return showInput;
    }
}
