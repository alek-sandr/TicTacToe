package com.kodingen.cetrin.model;

import com.kodingen.cetrin.player.ComputerPlayer;
import com.kodingen.cetrin.player.Player;

import java.util.ArrayList;
import java.util.List;

public class GameModel extends Model {
    public static final int FIELD_SIZE = 3;
    public static final int X = 1, O = -1, EMPTY = 0;

    private int[][] gameField = new int[FIELD_SIZE][FIELD_SIZE];
    private List<Move> moves = new ArrayList<Move>(FIELD_SIZE * FIELD_SIZE);
    private Player xPlayer;
    private Player oPlayer;
    private Player currentPlayer;
    private Player winner;
    private boolean canUndo = false;

    public GameModel() {
    }

    public void setPlayers(Player xPlayer, Player oPlayer) {
        this.xPlayer = xPlayer;
        this.oPlayer = oPlayer;
        this.xPlayer.setGameModel(this);
        this.oPlayer.setGameModel(this);
        if (oPlayer.getClass() == ComputerPlayer.class) {
            canUndo = true;
        }
        currentPlayer = xPlayer;
    }

    public void clear() {
        xPlayer = null;
        oPlayer = null;
        currentPlayer = null;
        winner = null;
        moves.clear();
        canUndo = false;
        for (int x = 0; x < FIELD_SIZE; x++) {
            for (int y = 0; y < FIELD_SIZE; y++) {
                gameField[x][y] = EMPTY;
            }
        }
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    private Player getXPlayer() {
        return xPlayer;
    }

    private Player getOPlayer() {
        return oPlayer;
    }

    public boolean isMoveAvailable(int x, int y) {
        return x >= 0 && x < FIELD_SIZE && y >= 0 && y < FIELD_SIZE && gameField[x][y] == EMPTY;
    }

    public void makeMove(int x, int y) {
        assert moves.size() < FIELD_SIZE * FIELD_SIZE;

        gameField[x][y] = getCurrentPlayer().getSymbolCode();
        moves.add(new Move(x, y));
        checkWinner();
        currentPlayer = currentPlayer == xPlayer ? oPlayer : xPlayer;
        notifySubscribers();
    }

    public int movesCount() {
        return moves.size();
    }

    public Move getLastMove() {
        return moves.get(moves.size() - 1);
    }

    public Move getMove(int index) {
        return moves.get(index);
    }

    private void checkWinner() {
        int winSymptom;
        for (int x = 0; x < FIELD_SIZE; x++) {
            winSymptom = 0;
            for (int y = 0; y < FIELD_SIZE; y++) {
                winSymptom += gameField[x][y];
            }
            if (Math.abs(winSymptom) == FIELD_SIZE) {
                setWinner(winSymptom);
            }
        }
        for (int y = 0; y < FIELD_SIZE; y++) {
            winSymptom = 0;
            for (int x = 0; x < FIELD_SIZE; x++) {
                winSymptom += gameField[x][y];
            }
            if (Math.abs(winSymptom) == FIELD_SIZE) {
                setWinner(winSymptom);
            }
        }
        winSymptom = 0;
        for (int x = 0, y = 0; x < FIELD_SIZE; x++, y++) {
            winSymptom += gameField[x][y];
        }
        if (Math.abs(winSymptom) == FIELD_SIZE) {
            setWinner(winSymptom);
        }
        winSymptom = 0;
        for (int x = 0, y = FIELD_SIZE - 1; x < FIELD_SIZE; x++, y--) {
            winSymptom += gameField[x][y];
        }
        if (Math.abs(winSymptom) == FIELD_SIZE) {
            setWinner(winSymptom);
        }
    }

    private void setWinner(int code) {
        if (code == FIELD_SIZE) {
            winner = getXPlayer();
        } else if (code == -FIELD_SIZE) {
            winner = getOPlayer();
        } else {
            throw new IllegalArgumentException();
        }
    }

    public Player getWinner() {
        return winner;
    }

    public boolean hasWinner() {
        return winner != null;
    }

    public boolean hasMoreMoves() {
        return moves.size() < FIELD_SIZE * FIELD_SIZE;
    }

    public int getFieldCell(int x, int y) {
        return gameField[x][y];
    }

    public char getFieldCellChar(int x, int y) {
        int charCode = getFieldCell(x, y);
        switch (charCode) {
            case X: return 'X';
            case O: return 'O';
            default: return ' ';
        }
    }

    public int getFieldSize() {
        return FIELD_SIZE;
    }

    public boolean canDiscardLastPlayerMove() {
        return movesCount() >= 2 && canUndo;
    }

    public void discardLastPlayerMove() {
        if (canDiscardLastPlayerMove()) {
            Move t = moves.remove(movesCount() - 1);
            gameField[t.getX()][t.getY()] = EMPTY;
            t = moves.remove(movesCount() - 1);
            gameField[t.getX()][t.getY()] = EMPTY;
            notifySubscribers();
        }
    }
}
