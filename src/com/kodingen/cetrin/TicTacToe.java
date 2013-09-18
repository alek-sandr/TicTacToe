package com.kodingen.cetrin;

import com.kodingen.cetrin.controller.GameController;
import com.kodingen.cetrin.model.GameModel;
import com.kodingen.cetrin.view.ConsoleView;

public class TicTacToe {
    public static void main(String[] args) {
        GameController gc = new GameController(new GameModel(), new ConsoleView());
        gc.startGame();
    }
}
