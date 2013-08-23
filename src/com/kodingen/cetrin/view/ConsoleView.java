package com.kodingen.cetrin.view;

import com.kodingen.cetrin.model.GameModel;
import com.kodingen.cetrin.model.Move;
import com.kodingen.cetrin.controller.Command;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.*;

import java.io.IOException;
import java.util.Scanner;

public class ConsoleView extends View {
    public static final String ANSI_CLS = "\u001b[2J";
    public static final String ANSI_HOME = "\u001b[H";

    private Scanner in;

    public ConsoleView() {
        //this.gm = gm;
        //controller = new GameController(gm, this);
        in = new Scanner(System.in);
    }

    public void modelChanged() {
        clearConsole();
        showGameField();
        if (gm.getWinner() != null) {
            System.out.println("Congratulations to Player " + gm.getWinner().getSymbol() + "!");
            controller.execute(Command.END_GAME, null);
            return;
        } else if (!gm.hasMoreMoves()) {
            System.out.println("Draw!");
            controller.execute(Command.END_GAME, null);
            return;
        }
        controller.execute(Command.MOVE, null);
    }

    @Override
    public void showPlayerInputForm() {
        System.out.print("Player " + gm.getCurrentPlayer().getSymbol() + " turn (row column): ");
        String[] input = in.nextLine().trim().split(" ");
        processInput(input);
    }

    @Override
    public void showMessage(String message) {
        System.out.println(message);
    }

    private void showGameField() {
        //StringBuilder sb = new StringBuilder("*** TicTacToe Game ***\n   0  1  2\n");
        StringBuilder sb = new StringBuilder();
        sb.append(ansi().fg(Color.GREEN).a("* TicTacToe Game *").fg(Color.WHITE).a("\n    0  1  2\n"));
        for (int x = 0; x < gm.getFieldSize(); x++) {
            sb.append(' ').append(x).append(' ');
            for (int y = 0; y < gm.getFieldSize(); y++) {
                sb.append('[');
                //sb.append(gm.getFieldCellChar(x, y));
                Ansi.Color color = gm.getFieldCell(x, y) == GameModel.X ? Color.CYAN : Color.YELLOW;
                sb.append(ansi().fg(color).a(gm.getFieldCellChar(x, y)).fg(Color.WHITE));
                sb.append(']');
            }
            sb.append('\n');
        }
        AnsiConsole.systemInstall();
        System.out.println(sb.toString());
        AnsiConsole.systemUninstall();
        System.out.println("q - to quit; u - to undo last move in game with computer\n");
    }

    private void processInput(String[] input) {
//        System.out.print("Player " + gm.getCurrentPlayer().getSymbol() + " turn (row column): ");
//        String[] input = in.nextLine().trim().split(" ");
        switch (input.length) {
            case 1:
                processCommand(input[0]);
            case 2:
                processCoordinates(input);
            default:
                showMessage("Wrong number of parameters. Try again.");
                showPlayerInputForm();
        }
    }

    private boolean processCommand(String command) {
        if (command.equalsIgnoreCase("q")) {
            controller.execute(Command.QUIT, null);
            return true;
        }
        if (command.equalsIgnoreCase("u")) {
            controller.execute(Command.UNDO, null);
            return true;
        }
        System.out.println("No such command.");
        return false;
    }

    private void processCoordinates(String[] input) {
        assert input.length == 2;
        int x, y;
        try {
            x = Integer.parseInt(input[0]);
            y = Integer.parseInt(input[1]);
            if (gm.isMoveAvailable(x, y)) {
//                gm.makeMove(x, y);
                controller.execute(Command.MOVE, new Move(x, y));
            } else {
                showMessage("Wrong coordinates or this cell already occupied.");
                showPlayerInputForm();
            }
        } catch (NumberFormatException nfe) {
            showMessage("Entered coordinates are not integer numbers.");
            showPlayerInputForm();
        }
    }

    public void start() {
        System.out.println("Starting new game.\nSelect game type.");
        System.out.println("1) Real player - Real player");
        System.out.println("2) Real player - Computer");
        System.out.println("3) Create network game");
        System.out.println("4) Connect to network game");
        System.out.print("Enter game type: ");
        int gameType = in.nextInt();
        in.nextLine(); //read next line symbol
        while (gameType < 1 || gameType > 4) {
            System.out.println("Wrong input. Try again: ");
            gameType = in.nextInt();
        }
        controller.execute(Command.NEW_GAME, gameType);
    }

    @Override
    public String askForInput(String message) {
        System.out.print(message);
        return in.nextLine();
    }

    @Override
    public void askForRepeat() {
        System.out.print("Play again?(y/n): ");
        String answer = in.nextLine();
        if (answer.equalsIgnoreCase("y")) {
            start();
        } else if (answer.equalsIgnoreCase("n")) {
            controller.execute(Command.QUIT, null);
        } else {
            showMessage("Wrong input. Try again.");
            askForRepeat();
        }
    }

    private void clearConsole() {
        try {
            String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                AnsiConsole.out.println(ANSI_CLS);
                AnsiConsole.out.println(ANSI_HOME);
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (IOException e) {
            // ignore
        }
    }
}
