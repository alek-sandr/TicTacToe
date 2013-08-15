package com.kodingen.cetrin;

public class NetworkPlayer extends Player {

    public NetworkPlayer(int symbol) {
        this(symbol, false);
    }

    private NetworkPlayer(int symbol, boolean showInputForm) {
        super(symbol, showInputForm);
    }

    @Override
    public void makeTurn(GameModel model) {
        //TODO: implement network game
    }
}
