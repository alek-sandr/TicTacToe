package com.kodingen.cetrin.controller;

import com.kodingen.cetrin.model.GameModel;
import com.kodingen.cetrin.model.Move;
import com.kodingen.cetrin.player.ComputerPlayer;
import com.kodingen.cetrin.player.NetworkPlayer;
import com.kodingen.cetrin.player.Player;
import com.kodingen.cetrin.player.RealPlayer;
import com.kodingen.cetrin.view.View;

import java.io.IOException;

public class GameController implements IController {
    public static final int HOTSEAT = 1, AIGAME = 2, LANGAME_SERVER = 3, LANGAME_CLIENT = 4;
    GameModel gm = null;
    View view;

    public GameController(GameModel gm, View view) {
        this.gm = gm;
        this.view = view;
        this.view.setModel(gm);
        this.view.setController(this);
    }

    public void startGame() {
        view.start();
    }

    @Override
    public void execute(Command command, Object data) {
        switch (command) {
            case QUIT:
                quit();
                break;
            case UNDO:
                undo();
                break;
            case MOVE:
                move(data);
                break;
            case NEW_GAME:
                newGame(data);
                break;
            case END_GAME:
                endGame();
                break;
            default:
                throw new IllegalArgumentException("Command doesn't exist!");
        }
    }

    private void quit() {
        System.exit(0);
    }

    private void undo() {
        if (gm.canDiscardLastPlayerMove()) {
            gm.discardLastPlayerMove();
        } else {
            view.showMessage("Unavailable command. Try again.");
            view.askPlayerForMove();
        }
    }

    private void move(Object data) {
        Move move = null;
        if (gm.getCurrentPlayer().showInputForm()) {
            if (data == null) {
                view.askPlayerForMove();
                return;
            }
            if (data.getClass() != Move.class) {
                throw new NullPointerException("Wrong data transfered to GameController in method move()");
            }
            move = (Move) data;
        } else {
            view.showMessage("Waiting for Player " + gm.getCurrentPlayer().getSymbol() + " move...");
            try {
                move = gm.getCurrentPlayer().getMove();
            } catch (IOException e) {
                view.showMessage(e.getMessage());
                quit();
            }
        }
        assert move != null;
        if (gm.isMoveAvailable(move.getX(), move.getY())) {
            gm.makeMove(move.getX(), move.getY());
        } else {
            view.showMessage("Wrong coordinates. Try again.");
            view.askPlayerForMove();
        }
    }

    private void newGame(Object data) {
        if (data == null || data.getClass() != Integer.class) {
            throw new IllegalArgumentException("Argument must be Integer type");
        }
        int type = (Integer) data;
        switch (type) {
            case HOTSEAT:
                newHotseatGame();
                break;
            case AIGAME:
                newGameWithAI();
                break;
            case LANGAME_SERVER:
                newNetworkServerGame();
                break;
            case LANGAME_CLIENT:
                newNetworkClientGame();
                break;
            default:
                view.showMessage("Illegal game mode. Try again.");
                view.start();
        }
    }

    private void newHotseatGame() {
        gm.clear();
        gm.setPlayers(new RealPlayer(GameModel.X), new RealPlayer(GameModel.O));
        gm.subscribe(view);
    }

    private void newGameWithAI() {
        gm.clear();
        gm.setPlayers(new RealPlayer(GameModel.X), new ComputerPlayer());
        gm.subscribe(view);
    }

    private void newNetworkServerGame() {
        gm.clear();
        try {
            view.showMessage("Waiting for opponent connection...");
            Player oPlayer = new NetworkPlayer(GameModel.O, NetworkPlayer.SERVER, null);
            view.showMessage("Opponent connected. Starting game...");
            gm.setPlayers(new RealPlayer(GameModel.X), oPlayer);
            gm.subscribe(view);
        } catch (IOException e) {
            view.showMessage("Unable to start server. Terminating...");
            endGame();
        }
    }

    private void newNetworkClientGame() {
        gm.clear();
        String address = view.askForInput("Enter server address: ");
        try {
            view.showMessage("Trying to connect to " + address + "...");
            Player xPlayer = new NetworkPlayer(GameModel.X, NetworkPlayer.CLIENT, address);
            view.showMessage("Connected to " + address + ". Starting game...");
            gm.setPlayers(xPlayer, new RealPlayer(GameModel.O));
            gm.subscribe(view);
        } catch (IOException e) {
            view.showMessage(e.getMessage());
            endGame();
        }
    }

    private void endGame() {
        view.askForGamelog();
        gm.unsubscribe(view);
        view.askForRepeat();
    }
}
