package com.kodingen.cetrin;

public abstract class Player {
    private final int symbol;
    private boolean showInput;

    public Player(int symbol) {
        this(symbol, true);
    }
    public Player(int symbol, boolean showInputForm) {
        this.symbol = symbol;
        showInput = showInputForm;
    }

    public int getSymbolCode() {
        return symbol;
    }

    public char getSymbol() {
        return symbol == GameModel.X ? 'X' : 'O';
    }
    public abstract void makeTurn(GameModel model);

    public boolean showInputForm() {
        return showInput;
    }
}
