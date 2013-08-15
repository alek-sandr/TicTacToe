package com.kodingen.cetrin;

public class ComputerPlayer extends Player {

    public ComputerPlayer() {
        this(GameModel.O, false);
    }

    private ComputerPlayer(int symbol, boolean showInputForm) {
        super(symbol, showInputForm);
    }

    @Override
    public void makeTurn(GameModel gm) {
        if (gm.getWinner() != null) return;
        if (!gm.hasMoreTurns()) return;
        // if we can immediately win we do it
        if (tryWin(gm)) return;
        // or try prevent other player to win
        if (tryPreventOtherWin(gm)) return;

        // Если крестики сделали первый ход в центр, до конца игры ходить в любой угол,
        // а если это невозможно — в любую клетку
        if (gm.getTurn(0).isCenterTurn()) {
            if (tryCorners(gm)) return;
            if (tryAnyEmptyPosition(gm)) return;
        }
        // Если крестики сделали первый ход в угол, ответить ходом в центр.
        // Следующим ходом занять угол, противоположный первому ходу крестиков, а если это невозможно — пойти на сторону.
        if (gm.getTurn(0).isCornerTurn()) {
            if (tryCenter(gm)) return;
            if (gm.turnsCount() == 3) {
                if (tryOppositeCorner(gm.getTurn(0), gm)) return;
                if (trySides(gm)) return;
            }
        }

        if (gm.turnsCount() <= 3 && gm.getTurn(0).isSideTurn()) {
            // Если крестики сделали первый ход на сторону, ответить ходом в центр
            if (tryCenter(gm)) return;
            // Если следующий ход крестиков — в угол, занять противоположный угол
            if (gm.turnsCount() == 3 && gm.getTurn(2).isCornerTurn()) {
                if (tryOppositeCorner(gm.getTurn(2), gm)) return;
            }
            // Если следующий ход крестиков — на противоположную сторону, пойти в любой угол
            if (gm.turnsCount() == 3) {
                if (Math.abs(gm.getLastTurn().getX() - gm.getFieldSize() + 1) == gm.getTurn(0).getX() &&
                        Math.abs(gm.getLastTurn().getY() - gm.getFieldSize() + 1) == gm.getTurn(0).getY()) {
                    if (tryCorners(gm)) return;
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
                if (tryCell(x, y, gm)) return;
            }
        }

        // go to any empty position
        tryAnyEmptyPosition(gm);
    }

    private boolean tryWin(GameModel gm) {
        for (int i = 0; i < gm.getFieldSize(); i++) {
            if (checkHorizontalLineForWin(i, gm)) return true;
            if (checkVerticalLineForWin(i, gm)) return true;
        }
        return checkDiagonalsForWin(gm);
    }

    private boolean checkHorizontalLineForWin(int row, GameModel gm) {
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
            gm.makeTurn(emptyCellNumber, row);
            return true;
        }
        return false;
    }

    private boolean checkVerticalLineForWin(int column, GameModel gm) {
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
            gm.makeTurn(column, emptyCellNumber);
            return true;
        }
        return false;
    }

    private boolean checkDiagonalsForWin(GameModel gm) {
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
            gm.makeTurn(emptyCellNumber, emptyCellNumber);
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
            gm.makeTurn(emptyCellNumber, gm.getFieldSize() - 1 - emptyCellNumber);
            return true;
        }
        return false;
    }

    private boolean tryPreventOtherWin(GameModel gm) {
        for (int i = 0; i < gm.getFieldSize(); i++) {
            if (checkHorizontalLineForPrevent(i, gm)) return true;
            if (checkVerticalLineForPrevent(i, gm)) return true;
        }
        return checkDiagonalsForPrevent(gm);
    }

    private boolean checkHorizontalLineForPrevent(int row, GameModel gm) {
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
            gm.makeTurn(emptyCellNumber, row);
            return true;
        }
        return false;
    }

    private boolean checkVerticalLineForPrevent(int column, GameModel gm) {
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
            gm.makeTurn(column, emptyCellNumber);
            return true;
        }
        return false;
    }

    private boolean checkDiagonalsForPrevent(GameModel gm) {
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
            gm.makeTurn(emptyCellNumber, emptyCellNumber);
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
            gm.makeTurn(emptyCellNumber, gm.getFieldSize() - 1 - emptyCellNumber);
            return true;
        }
        return false;
    }

    private boolean tryCenter(GameModel gm) {
        int centerPosition = gm.getFieldSize() / 2;
        if (gm.getFieldCell(centerPosition, centerPosition) == 0) {
            gm.makeTurn(centerPosition, centerPosition);
            return true;
        }
        return false;
    }

    private boolean tryOppositeCorner(GameModel.Turn turn, GameModel gm) {
        if (turn.isCornerTurn()) {
            if (tryCell(Math.abs(turn.getX() - gm.getFieldSize() + 1),
                    Math.abs(turn.getY() - gm.getFieldSize() + 1), gm)) {
                return true;
            }
        }
        return false;
    }

    private boolean tryCorners(GameModel gm) {
        if (tryCell(0, 0, gm)) {
            return true;
        }
        if (tryCell(0, gm.getFieldSize() - 1, gm)) {
            return true;
        }
        if (tryCell(gm.getFieldSize() - 1, 0, gm)) {
            return true;
        }
        return tryCell(gm.getFieldSize() - 1, gm.getFieldSize() - 1, gm);
    }

    private boolean trySides(GameModel gm) {
        if (tryCell(gm.getFieldSize() / 2, 0, gm)) {
            return true;
        }
        if (tryCell(gm.getFieldSize() - 1, gm.getFieldSize() / 2, gm)) {
            return true;
        }
        if (tryCell(0, gm.getFieldSize() / 2, gm)) {
            return true;
        }
        return tryCell(gm.getFieldSize() / 2, gm.getFieldSize() - 1, gm);
    }

    private boolean tryCell(int x, int y, GameModel gm) {
        if (gm.getFieldCell(x, y) == 0) {
            gm.makeTurn(x, y);
            return true;
        }
        return false;
    }

    private boolean tryAnyEmptyPosition(GameModel gm) {
        for (int x = 0; x < gm.getFieldSize(); x++) {
            for (int y = 0; y < gm.getFieldSize(); y++) {
                if (gm.getFieldCell(x, y) == 0) {
                    gm.makeTurn(x, y);
                    return true;
                }
            }
        }
        return false;
    }

}
