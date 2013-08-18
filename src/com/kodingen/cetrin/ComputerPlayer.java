package com.kodingen.cetrin;

public class ComputerPlayer extends Player {

    public ComputerPlayer() {
        this(GameModel.O, false);
    }

    private ComputerPlayer(int symbol, boolean showInputForm) {
        super(symbol, showInputForm);
    }

    @Override
    public void makeMove() {
        if (gm.hasWinner()) return;
        if (!gm.hasMoreTurns()) return;
        // if we can immediately win we do it
        if (tryWin()) return;
        // or try prevent other player to win
        if (tryPreventOtherWin()) return;

        // Если крестики сделали первый ход в центр, до конца игры ходить в любой угол,
        // а если это невозможно — в любую клетку
        if (gm.getTurn(0).isCenterTurn()) {
            if (tryCorners()) return;
            if (tryAnyEmptyPosition()) return;
        }
        // Если крестики сделали первый ход в угол, ответить ходом в центр.
        // Следующим ходом занять угол, противоположный первому ходу крестиков, а если это невозможно — пойти на сторону.
        if (gm.getTurn(0).isCornerTurn()) {
            if (tryCenter()) return;
            if (gm.turnsCount() == 3) {
                if (tryOppositeCorner(gm.getTurn(0), gm)) return;
                if (trySides()) return;
            }
        }

        if (gm.turnsCount() <= 3 && gm.getTurn(0).isSideTurn()) {
            // Если крестики сделали первый ход на сторону, ответить ходом в центр
            if (tryCenter()) return;
            // Если следующий ход крестиков — в угол, занять противоположный угол
            if (gm.turnsCount() == 3 && gm.getTurn(2).isCornerTurn()) {
                if (tryOppositeCorner(gm.getTurn(2), gm)) return;
            }
            // Если следующий ход крестиков — на противоположную сторону, пойти в любой угол
            if (gm.turnsCount() == 3) {
                if (Math.abs(gm.getLastTurn().getX() - gm.getFieldSize() + 1) == gm.getTurn(0).getX() &&
                        Math.abs(gm.getLastTurn().getY() - gm.getFieldSize() + 1) == gm.getTurn(0).getY()) {
                    if (tryCorners()) return;
                }
            }
            // Если следующий ход крестиков — на сторону рядом с их первым ходом,
            // пойти в угол рядом с обоими крестиками
            if (gm.turnsCount() == 3 && gm.getLastTurn().isSideTurn() &&
                    gm.getTurn(0).getX() != gm.getLastTurn().getX() && gm.getTurn(0).getY() != gm.getLastTurn().getY()) {
                int x = 0, y = 0;
                if (gm.getTurn(0).getX() == 0 || gm.getTurn(0).getX() == gm.getFieldSize() - 1) {
                    x = gm.getTurn(0).getX();
                } else if (gm.getLastTurn().getX() == 0 || gm.getLastTurn().getX() == gm.getFieldSize() - 1) {
                    x = gm.getLastTurn().getX();
                }
                if (gm.getTurn(0).getY() == 0 || gm.getTurn(0).getY() == gm.getFieldSize() - 1) {
                    y = gm.getTurn(0).getY();
                } else if (gm.getLastTurn().getY() == 0 || gm.getLastTurn().getY() == gm.getFieldSize() - 1) {
                    y = gm.getLastTurn().getY();
                }
                if (tryCell(x, y)) return;
            }
        }

        // go to any empty position
        tryAnyEmptyPosition();
    }

    private boolean tryWin() {
        for (int i = 0; i < gm.getFieldSize(); i++) {
            if (checkHorizontalLineForWin(i)) return true;
            if (checkVerticalLineForWin(i)) return true;
        }
        return checkDiagonalsForWin();
    }

    private boolean checkHorizontalLineForWin(int row) {
        int myCellsCount = 0;
        int emptyCellNumber = -1;
        for (int i = 0; i < gm.getFieldSize(); i++) {
            if (gm.getFieldCell(i, row) == this.getSymbolCode()) {
                myCellsCount++;
            } else if (gm.getFieldCell(i, row) == GameModel.EMPTY) {
                emptyCellNumber = i;
            }
        }
        if (myCellsCount == 2 && emptyCellNumber != -1) {
            gm.makeMove(emptyCellNumber, row);
            return true;
        }
        return false;
    }

    private boolean checkVerticalLineForWin(int column) {
        int myCellsCount = 0;
        int emptyCellNumber = -1;
        for (int i = 0; i < gm.getFieldSize(); i++) {
            if (gm.getFieldCell(column, i) == this.getSymbolCode()) {
                myCellsCount++;
            } else if (gm.getFieldCell(column, i) == GameModel.EMPTY) {
                emptyCellNumber = i;
            }
        }
        if (myCellsCount == 2 && emptyCellNumber != -1) {
            gm.makeMove(column, emptyCellNumber);
            return true;
        }
        return false;
    }

    private boolean checkDiagonalsForWin() {
        int myCellsCount = 0;
        int emptyCellNumber = -1;
        for (int i = 0; i < gm.getFieldSize(); i++) {
            if (gm.getFieldCell(i, i) == this.getSymbolCode()) {
                myCellsCount++;
            } else if (gm.getFieldCell(i, i) == GameModel.EMPTY) {
                emptyCellNumber = i;
            }
        }
        if (myCellsCount == 2 && emptyCellNumber != -1) {
            gm.makeMove(emptyCellNumber, emptyCellNumber);
            return true;
        }
        myCellsCount = 0;
        emptyCellNumber = -1;
        for (int i = 0; i < gm.getFieldSize(); i++) {
            if (gm.getFieldCell(i, gm.getFieldSize() - 1 - i) == this.getSymbolCode()) {
                myCellsCount++;
            } else if (gm.getFieldCell(i, gm.getFieldSize() - 1 - i) == GameModel.EMPTY){
                emptyCellNumber = i;
            }
        }
        if (myCellsCount == 2 && emptyCellNumber != -1) {
            gm.makeMove(emptyCellNumber, gm.getFieldSize() - 1 - emptyCellNumber);
            return true;
        }
        return false;
    }

    private boolean tryPreventOtherWin() {
        for (int i = 0; i < gm.getFieldSize(); i++) {
            if (checkHorizontalLineForPrevent(i)) return true;
            if (checkVerticalLineForPrevent(i)) return true;
        }
        return checkDiagonalsForPrevent();
    }

    private boolean checkHorizontalLineForPrevent(int row) {
        int oppositeCellsCount = 0;
        int emptyCellNumber = -1;
        for (int i = 0; i < gm.getFieldSize(); i++) {
            if (gm.getFieldCell(i, row) == -this.getSymbolCode()) {
                oppositeCellsCount++;
            } else if (gm.getFieldCell(i, row) == GameModel.EMPTY) {
                emptyCellNumber = i;
            }
        }
        if (oppositeCellsCount == 2 && emptyCellNumber != -1) {
            gm.makeMove(emptyCellNumber, row);
            return true;
        }
        return false;
    }

    private boolean checkVerticalLineForPrevent(int column) {
        int oppositeCellsCount = 0;
        int emptyCellNumber = -1;
        for (int i = 0; i < gm.getFieldSize(); i++) {
            if (gm.getFieldCell(column, i) == -this.getSymbolCode()) {
                oppositeCellsCount++;
            } else if (gm.getFieldCell(column, i) == GameModel.EMPTY) {
                emptyCellNumber = i;
            }
        }
        if (oppositeCellsCount == 2 && emptyCellNumber != -1) {
            gm.makeMove(column, emptyCellNumber);
            return true;
        }
        return false;
    }

    private boolean checkDiagonalsForPrevent() {
        int oppositeCellsCount = 0;
        int emptyCellNumber = -1;
        for (int i = 0; i < gm.getFieldSize(); i++) {
            if (gm.getFieldCell(i, i) == -this.getSymbolCode()) {
                oppositeCellsCount++;
            } else if (gm.getFieldCell(i, i) == GameModel.EMPTY) {
                emptyCellNumber = i;
            }
        }
        if (oppositeCellsCount == 2 && emptyCellNumber != -1) {
            gm.makeMove(emptyCellNumber, emptyCellNumber);
            return true;
        }
        oppositeCellsCount = 0;
        emptyCellNumber = -1;
        for (int i = 0; i < gm.getFieldSize(); i++) {
            if (gm.getFieldCell(i, gm.getFieldSize() - 1 - i) == -this.getSymbolCode()) {
                oppositeCellsCount++;
            } else if (gm.getFieldCell(i, gm.getFieldSize() - 1 - i) == GameModel.EMPTY) {
                emptyCellNumber = i;
            }
        }
        if (oppositeCellsCount == 2 && emptyCellNumber != -1) {
            gm.makeMove(emptyCellNumber, gm.getFieldSize() - 1 - emptyCellNumber);
            return true;
        }
        return false;
    }

    private boolean tryCenter() {
        int centerPosition = gm.getFieldSize() / 2;
        if (gm.getFieldCell(centerPosition, centerPosition) == 0) {
            gm.makeMove(centerPosition, centerPosition);
            return true;
        }
        return false;
    }

    private boolean tryOppositeCorner(GameModel.Turn turn, GameModel gm) {
        if (turn.isCornerTurn()) {
            if (tryCell(Math.abs(turn.getX() - gm.getFieldSize() + 1),
                    Math.abs(turn.getY() - gm.getFieldSize() + 1))) {
                return true;
            }
        }
        return false;
    }

    private boolean tryCorners() {
        if (tryCell(0, 0)) {
            return true;
        }
        if (tryCell(0, gm.getFieldSize() - 1)) {
            return true;
        }
        if (tryCell(gm.getFieldSize() - 1, 0)) {
            return true;
        }
        return tryCell(gm.getFieldSize() - 1, gm.getFieldSize() - 1);
    }

    private boolean trySides() {
        if (tryCell(gm.getFieldSize() / 2, 0)) {
            return true;
        }
        if (tryCell(gm.getFieldSize() - 1, gm.getFieldSize() / 2)) {
            return true;
        }
        if (tryCell(0, gm.getFieldSize() / 2)) {
            return true;
        }
        return tryCell(gm.getFieldSize() / 2, gm.getFieldSize() - 1);
    }

    private boolean tryCell(int x, int y) {
        if (gm.getFieldCell(x, y) == 0) {
            gm.makeMove(x, y);
            return true;
        }
        return false;
    }

    private boolean tryAnyEmptyPosition() {
        for (int x = 0; x < gm.getFieldSize(); x++) {
            for (int y = 0; y < gm.getFieldSize(); y++) {
                if (gm.getFieldCell(x, y) == 0) {
                    gm.makeMove(x, y);
                    return true;
                }
            }
        }
        return false;
    }

}
