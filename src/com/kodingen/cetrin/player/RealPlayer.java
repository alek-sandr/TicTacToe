package com.kodingen.cetrin.player;

import com.kodingen.cetrin.model.Move;

public class RealPlayer extends Player {

    public RealPlayer(int symbol) {
        this(symbol, true);
    }

    private RealPlayer(int symbol, boolean showInputForm) {
        super(symbol, showInputForm);
    }

    @Override
    public Move getMove() {
        return null;
    }
}
