package com.kodingen.cetrin;

public class RealPlayer extends Player {

    public RealPlayer(int symbol) {
        this(symbol, true);
    }

    private RealPlayer(int symbol, boolean showInputForm) {
        super(symbol, showInputForm);
    }

    @Override
    public void makeTurn(GameModel model) {
        // do nothing. player enter data to view
    }
}
